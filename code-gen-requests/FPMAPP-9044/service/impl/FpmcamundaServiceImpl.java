package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.dto.ApprovalRoutingResponseDTO;
import com.fpm.service.FpmcamundaService;
import com.fpm.util.EmailNotificationUtil;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FpmcamundaServiceImpl implements FpmcamundaService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmailNotificationUtil emailNotificationUtil;

    // STORY: FPMAPP-9044 - Route approval request based on role and financial thresholds
    @Override
    public ApprovalRoutingResponseDTO routeApproval(ApprovalRequestDTO approvalRequestDTO) {
        ApprovalRoutingResponseDTO response = new ApprovalRoutingResponseDTO();

        // TODO: Fetch role mappings and financial thresholds from DB/configuration
        // TODO: Implement hierarchical approval logic based on requestType and amount

        // Example pseudo-logic:
        String nextApproverRole = determineNextApproverRole(approvalRequestDTO.getRequestType(), approvalRequestDTO.getAmount());

        if (nextApproverRole == null) {
            response.setRoutingMessage("No approver found for the given amount and request type.");
            return response;
        }

        // TODO: Query user(s) with nextApproverRole from user management system or DB
        String nextApproverUserId = findUserIdByRole(nextApproverRole);

        // TODO: Start or continue Camunda workflow with routing info
        startOrContinueWorkflow(approvalRequestDTO, nextApproverRole, nextApproverUserId);

        // Send notifications
        emailNotificationUtil.sendApprovalNotification(nextApproverUserId, approvalRequestDTO.getRequestId(), approvalRequestDTO.getAmount());
        // TODO: Trigger in-app notification for nextApproverUserId

        response.setNextApproverRole(nextApproverRole);
        response.setNextApproverUserId(nextApproverUserId);
        response.setRoutingMessage("Approval request routed successfully.");

        return response;
    }

    // STORY: FPMAPP-9044 - Determine next approver role based on request type and amount
    private String determineNextApproverRole(String requestType, Double amount) {
        // TODO: Implement actual logic to determine next approver role based on thresholds and role mappings
        // This is a stub example:
        if ("dealsheet".equalsIgnoreCase(requestType)) {
            if (amount <= 10000) {
                return "ROLE_FINANCIAL_ANALYST";
            } else if (amount <= 50000) {
                return "ROLE_FINANCE_MANAGER";
            } else {
                return "ROLE_FINANCE_DIRECTOR";
            }
        } else if ("staffing".equalsIgnoreCase(requestType)) {
            if (amount <= 5000) {
                return "ROLE_PROJECT_MANAGER";
            } else {
                return "ROLE_FINANCE_MANAGER";
            }
        } else if ("travel".equalsIgnoreCase(requestType)) {
            if (amount <= 2000) {
                return "ROLE_PROJECT_MANAGER";
            } else {
                return "ROLE_FINANCE_MANAGER";
            }
        }
        return null;
    }

    // STORY: FPMAPP-9044 - Find user ID by role
    private String findUserIdByRole(String role) {
        // TODO: Implement user lookup by role from user management or DB
        // Stub: return dummy user id
        switch (role) {
            case "ROLE_FINANCIAL_ANALYST":
                return "user_analyst_01";
            case "ROLE_FINANCE_MANAGER":
                return "user_manager_01";
            case "ROLE_FINANCE_DIRECTOR":
                return "user_director_01";
            case "ROLE_PROJECT_MANAGER":
                return "user_pm_01";
            default:
                return null;
        }
    }

    // STORY: FPMAPP-9044 - Start or continue Camunda workflow with approval routing info
    private void startOrContinueWorkflow(ApprovalRequestDTO approvalRequestDTO, String nextApproverRole, String nextApproverUserId) {
        // TODO: Implement Camunda workflow integration
        // Example: start process instance or signal task

        // Check if process instance exists for requestId
        List<Task> tasks = taskService.createTaskQuery()
                .processVariableValueEquals("requestId", approvalRequestDTO.getRequestId())
                .list();

        if (tasks.isEmpty()) {
            // Start new process instance
            runtimeService.startProcessInstanceByKey("approvalWorkflowProcess", Map.of(
                    "requestId", approvalRequestDTO.getRequestId(),
                    "amount", approvalRequestDTO.getAmount(),
                    "requestType", approvalRequestDTO.getRequestType(),
                    "nextApproverRole", nextApproverRole,
                    "nextApproverUserId", nextApproverUserId
            ));
        } else {
            // Continue existing task(s) by setting variables and completing
            for (Task task : tasks) {
                taskService.setVariable(task.getId(), "nextApproverRole", nextApproverRole);
                taskService.setVariable(task.getId(), "nextApproverUserId", nextApproverUserId);
                taskService.complete(task.getId());
            }
        }
    }

}