package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.model.Approval;
import com.fpm.repository.ApprovalRepository;
import com.fpm.service.FpmcamundaService;
import com.fpm.util.NotificationUtil;
import com.fpm.util.ResponseWrapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FpmcamundaServiceImpl implements FpmcamundaService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private NotificationUtil notificationUtil;

    // STORY: FPMAPP-9039 - Process approval with hierarchical role-based routing, financial thresholds, delegation, notifications, and audit logging
    @Override
    @Transactional
    public ResponseWrapper processApproval(ApprovalRequestDTO approvalRequest) {
        String approvalId = approvalRequest.getApprovalId();
        String userRole = approvalRequest.getUserRole();
        BigDecimal amount = approvalRequest.getAmount();
        Map<String, Object> delegationInfo = approvalRequest.getDelegationInfo();

        // Fetch approval entity
        Approval approval = approvalRepository.findByApprovalId(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found for id: " + approvalId));

        // Validate delegation rules
        boolean delegationFlag = false;
        if (delegationInfo != null && !delegationInfo.isEmpty()) {
            // TODO: Implement delegation authorization check based on userRole and delegationInfo
            delegationFlag = checkDelegationAuthorization(userRole, delegationInfo);
            if (!delegationFlag) {
                throw new SecurityException("Unauthorized delegation attempt by role: " + userRole);
            }
        }

        // Determine next approval role based on amount and hierarchical thresholds
        String nextApprovalRole = determineNextApprovalRole(userRole, amount);

        // Update approval entity
        approval.setApprovalRole(nextApprovalRole);
        approval.setDelegationFlag(delegationFlag);
        approval.setFinancialThreshold(amount);
        approval.setAmount(amount);
        approval.setStatus("PENDING_APPROVAL");
        approval.setUpdatedAt(LocalDateTime.now());

        approvalRepository.save(approval);

        // Log approval or delegation action
        logApprovalAction(approvalId, userRole, amount, delegationFlag, delegationInfo);

        // Send notifications
        notificationUtil.sendApprovalNotification(nextApprovalRole, approvalId, amount, delegationFlag, delegationInfo);

        // TODO: Integrate with Camunda workflow engine to route approval process accordingly

        ResponseWrapper response = new ResponseWrapper();
        response.setMessage("Approval processed and routed to role: " + nextApprovalRole);
        response.setData(Map.of(
                "approvalId", approvalId,
                "nextApprovalRole", nextApprovalRole,
                "delegationFlag", delegationFlag
        ));
        return response;
    }

    // STORY: FPMAPP-9039 - Check if delegation is authorized for the given user role and delegation info
    private boolean checkDelegationAuthorization(String userRole, Map<String, Object> delegationInfo) {
        // TODO: Implement business logic to verify if userRole is authorized to delegate
        // For example, only roles "FINANCE_MANAGER" and above can delegate
        if (userRole == null) return false;
        switch (userRole) {
            case "FINANCE_MANAGER":
            case "FINANCE_DIRECTOR":
            case "CFO":
                return true;
            default:
                return false;
        }
    }

    // STORY: FPMAPP-9039 - Determine next approval role based on current role and financial amount thresholds
    private String determineNextApprovalRole(String currentUserRole, BigDecimal amount) {
        // TODO: Implement hierarchical role mapping and financial threshold logic
        // Example logic:
        // If amount <= 10,000 -> Approver role: "FINANCE_ANALYST"
        // If amount <= 50,000 -> Approver role: "FINANCE_MANAGER"
        // If amount <= 200,000 -> Approver role: "FINANCE_DIRECTOR"
        // Else -> Approver role: "CFO"

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.valueOf(10_000)) <= 0) {
            return "FINANCE_ANALYST";
        } else if (amount.compareTo(BigDecimal.valueOf(50_000)) <= 0) {
            return "FINANCE_MANAGER";
        } else if (amount.compareTo(BigDecimal.valueOf(200_000)) <= 0) {
            return "FINANCE_DIRECTOR";
        } else {
            return "CFO";
        }
    }

    // STORY: FPMAPP-9039 - Log approval and delegation actions with timestamps and user details
    private void logApprovalAction(String approvalId, String userRole, BigDecimal amount, boolean delegationFlag, Map<String, Object> delegationInfo) {
        // TODO: Implement audit logging to database or external audit system
        // Example: log to console for now
        String logMsg = String.format("ApprovalId: %s, UserRole: %s, Amount: %s, Delegation: %s, DelegationInfo: %s, Timestamp: %s",
                approvalId, userRole, amount.toPlainString(), delegationFlag, delegationInfo, LocalDateTime.now());
        System.out.println(logMsg);
    }
}
