package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.model.ApprovalActionType;
import com.fpm.model.ApprovalRecord;
import com.fpm.repository.ApprovalRecordRepository;
import com.fpm.service.ApprovalService;
import com.fpm.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;

    @Autowired
    private NotificationUtil notificationUtil;

    // STORY: FPMAPP-9033 - Process approval with hierarchical role-based routing, thresholds, delegation enforcement
    @Override
    @Transactional
    public Map<String, String> processApproval(ApprovalRequestDTO request) {
        Map<String, String> response = new HashMap<>();

        String userRole = request.getUserRole();
        BigDecimal financialAmount = request.getFinancialAmount();
        Map<String, Object> delegationInfo = request.getDelegationInfo();

        // TODO: Fetch role hierarchy and financial thresholds from config or DB
        // For demo, hardcoded thresholds and roles
        Map<String, BigDecimal> roleThresholds = Map.of(
            "JuniorApprover", new BigDecimal("1000"),
            "SeniorApprover", new BigDecimal("10000"),
            "Manager", new BigDecimal("100000")
        );

        // Determine required approval level based on financialAmount
        String requiredApprovalRole = determineApprovalRole(financialAmount, roleThresholds);

        // Check if userRole is authorized to approve this amount
        boolean canApprove = canUserApprove(userRole, requiredApprovalRole, roleThresholds);

        // Handle delegation
        boolean delegationFlag = false;
        Long delegatedToUserId = null;
        String delegationStatus = "NO_DELEGATION";

        if (delegationInfo != null && delegationInfo.containsKey("delegationFlag") && (Boolean) delegationInfo.get("delegationFlag")) {
            delegationFlag = true;
            delegatedToUserId = delegationInfo.get("delegatedToUserId") instanceof Number ? ((Number) delegationInfo.get("delegatedToUserId")).longValue() : null;

            // TODO: Enforce delegation rules based on roles
            if (!isDelegationAllowed(userRole, delegatedToUserId)) {
                response.put("routingDecision", "REJECTED");
                response.put("delegationStatus", "DELEGATION_NOT_ALLOWED");
                return response;
            }

            delegationStatus = "DELEGATED";
        }

        // Determine routing decision
        String routingDecision;
        ApprovalActionType actionType;

        if (canApprove) {
            routingDecision = "APPROVED";
            actionType = ApprovalActionType.APPROVE;
        } else if (delegationFlag) {
            routingDecision = "PENDING_DELEGATED_APPROVAL";
            actionType = ApprovalActionType.DELEGATE;
        } else {
            routingDecision = "PENDING_HIGHER_LEVEL_APPROVAL";
            actionType = ApprovalActionType.REJECT; // or PENDING, but per story we use REJECT for unauthorized
        }

        // Log approval action
        ApprovalRecord record = new ApprovalRecord();
        record.setApprovalRole(userRole);
        record.setFinancialThreshold(roleThresholds.getOrDefault(userRole, BigDecimal.ZERO));
        record.setDelegationFlag(delegationFlag);
        record.setDelegatedToUserId(delegatedToUserId);
        record.setTimestamp(LocalDateTime.now());
        record.setActionType(actionType);
        // TODO: Set userId from security context or request context
        record.setUserId(null); // TODO
        record.setFinancialAmount(financialAmount);
        record.setComments(null); // TODO: accept comments if needed

        approvalRecordRepository.save(record);

        // Trigger notifications
        notificationUtil.sendApprovalNotification(userRole, routingDecision, delegatedToUserId);

        response.put("routingDecision", routingDecision);
        response.put("delegationStatus", delegationStatus);
        return response;
    }

    // STORY: FPMAPP-9033 - Determine approval role based on financial amount and thresholds
    private String determineApprovalRole(BigDecimal amount, Map<String, BigDecimal> roleThresholds) {
        // TODO: Implement real hierarchical logic
        if (amount.compareTo(roleThresholds.get("JuniorApprover")) <= 0) {
            return "JuniorApprover";
        } else if (amount.compareTo(roleThresholds.get("SeniorApprover")) <= 0) {
            return "SeniorApprover";
        } else {
            return "Manager";
        }
    }

    // STORY: FPMAPP-9033 - Check if user role can approve given required role
    private boolean canUserApprove(String userRole, String requiredRole, Map<String, BigDecimal> roleThresholds) {
        // TODO: Implement role hierarchy comparison
        // For demo: roles ordered JuniorApprover < SeniorApprover < Manager
        int userRank = roleRank(userRole);
        int requiredRank = roleRank(requiredRole);
        return userRank >= requiredRank;
    }

    private int roleRank(String role) {
        return switch (role) {
            case "JuniorApprover" -> 1;
            case "SeniorApprover" -> 2;
            case "Manager" -> 3;
            default -> 0;
        };
    }

    // STORY: FPMAPP-9033 - Enforce delegation rules
    private boolean isDelegationAllowed(String userRole, Long delegatedToUserId) {
        // TODO: Implement delegation rules based on roles and delegated user
        // For demo, only SeniorApprover and Manager can delegate
        if (userRole == null || delegatedToUserId == null) {
            return false;
        }
        if (userRole.equals("SeniorApprover") || userRole.equals("Manager")) {
            return true;
        }
        return false;
    }
}
