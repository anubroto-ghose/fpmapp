package com.fpm.controller;

import com.fpm.dto.DelegationRequestDTO;
import com.fpm.model.DelegationLog;
import com.fpm.repository.DelegationLogRepository;
import jakarta.validation.Valid;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fpmcamunda")
public class FpmCamundaDelegationController {

    private static final Logger logger = LoggerFactory.getLogger(FpmCamundaDelegationController.class);

    private final ProcessEngine processEngine;
    private final DelegationLogRepository delegationLogRepository;

    private final Set<String> authorizedRoles;

    public FpmCamundaDelegationController(ProcessEngine processEngine,
                                          DelegationLogRepository delegationLogRepository,
                                          @Value("${fpmcamunda.delegation.authorizedRoles}") List<String> authorizedRoles) {
        this.processEngine = processEngine;
        this.delegationLogRepository = delegationLogRepository;
        this.authorizedRoles = authorizedRoles.stream().map(String::toUpperCase).collect(Collectors.toSet());
    }

    // STORY: FPMAPP-9047 - Endpoint to perform delegation actions with role validation and logging
    @PostMapping("/delegation")
    public ResponseEntity<?> performDelegation(@Valid @RequestBody DelegationRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication != null ? authentication.getName() : "UNKNOWN";

        // Check if current user is authorized to delegate
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authorizedRoles.contains(grantedAuthority.getAuthority().toUpperCase()));

        if (!isAuthorized) {
            logDelegationAction(request.getDelegatorId(), request.getDelegateeId(), request.getRequestId(), "FAILURE", "Unauthorized role for delegation");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User role not authorized to perform delegation.");
        }

        try {
            // TODO: Integrate with Camunda workflow engine to perform delegation enforcement
            // For example, find the task/request and set the delegatee as the assignee or add delegation
            // This is a placeholder for actual Camunda delegation logic

            // Example: delegate task to delegatee
            var taskService = processEngine.getTaskService();
            Task task = taskService.createTaskQuery().processInstanceBusinessKey(request.getRequestId()).singleResult();
            if (task == null) {
                logDelegationAction(request.getDelegatorId(), request.getDelegateeId(), request.getRequestId(), "FAILURE", "Task not found for requestId");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No task found for the given request ID.");
            }

            // Enforce delegation rules within Camunda
            // TODO: Add additional Camunda delegation rules enforcement here

            taskService.delegateTask(task.getId(), request.getDelegateeId());

            logDelegationAction(request.getDelegatorId(), request.getDelegateeId(), request.getRequestId(), "SUCCESS", null);
            return ResponseEntity.ok("Delegation performed successfully.");

        } catch (Exception e) {
            logger.error("Error during delegation", e);
            logDelegationAction(request.getDelegatorId(), request.getDelegateeId(), request.getRequestId(), "FAILURE", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delegation failed: " + e.getMessage());
        }
    }

    // STORY: FPMAPP-9047 - Helper method to log delegation actions
    private void logDelegationAction(String delegatorId, String delegateeId, String requestId, String actionResult, String reason) {
        DelegationLog log = new DelegationLog();
        log.setDelegatorId(delegatorId);
        log.setDelegateeId(delegateeId);
        log.setRequestId(requestId);
        log.setActionTimestamp(LocalDateTime.now());
        log.setActionResult(actionResult);
        log.setReason(reason);
        delegationLogRepository.save(log);
    }
}
