package com.fpm.controller;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.dto.ApprovalResponseDTO;
import com.fpm.service.DelegationService;
import com.fpm.service.NotificationService;
import com.fpm.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fpmcamunda")
public class FpmcamundaController {

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditService auditService;

    // STORY: FPMAPP-9038 - Modified approval endpoint to enforce delegation rules and trigger notifications
    @PostMapping("/approvals")
    public ResponseEntity<ApprovalResponseDTO> processApproval(@RequestBody ApprovalRequestDTO request) {
        ApprovalResponseDTO response = new ApprovalResponseDTO();

        // 1. Enforce delegation rules
        boolean delegationAllowed = true;
        if (request.getDelegationInfo() != null) {
            delegationAllowed = delegationService.isDelegationAllowed(request.getUserRole(), request.getDelegationInfo());
            if (!delegationAllowed) {
                response.setDelegationStatus("Delegation not allowed for role " + request.getUserRole());
                response.setNotificationStatus("No notifications sent due to delegation denial");
                return ResponseEntity.badRequest().body(response);
            }

            // Log delegation action
            auditService.logDelegationAction(request.getUserRole(), request.getDelegationInfo().getTargetUser());
        } else {
            response.setDelegationStatus("No delegation involved");
        }

        // 2. Process approval logic
        // TODO: Integrate with Camunda workflow engine approval processing

        // 3. Trigger notifications (email + in-app)
        boolean emailSent = notificationService.sendApprovalNotification(request);
        boolean inAppSent = notificationService.sendInAppNotification(request);

        // 4. Log notification delivery
        auditService.logNotificationDelivery(request.getUserRole(), emailSent, inAppSent);

        response.setNotificationStatus("Email sent: " + emailSent + ", In-app sent: " + inAppSent);
        response.setDelegationStatus(delegationAllowed ? "Delegation allowed and processed" : "Delegation denied");

        return ResponseEntity.ok(response);
    }
}