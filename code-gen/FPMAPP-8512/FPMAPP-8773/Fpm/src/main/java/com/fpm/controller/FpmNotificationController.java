package com.fpm.controller;

import com.fpm.dto.NotificationDto;
import com.fpm.service.FpmNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class FpmNotificationController {

    private final FpmNotificationService notificationService;

    public FpmNotificationController(FpmNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    // STORY: FPMAPP-8773 - Retrieve notifications for a user including pending approvals
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/approval/{approvalId}/action")
    // STORY: FPMAPP-8773 - Approve or reject an approval request and update notifications accordingly
    public ResponseEntity<NotificationDto> takeApprovalAction(
            @PathVariable Long approvalId,
            @RequestParam("userId") Long userId,
            @RequestParam("action") String action // expected values: "approve" or "reject"
    ) {
        NotificationDto updatedNotification = notificationService.processApprovalAction(approvalId, userId, action);
        return ResponseEntity.ok(updatedNotification);
    }
}