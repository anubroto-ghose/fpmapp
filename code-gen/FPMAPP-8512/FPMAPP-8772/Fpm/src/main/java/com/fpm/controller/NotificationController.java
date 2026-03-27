package com.fpm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpm.dto.NotificationDTO;
import com.fpm.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // STORY: FPMAPP-8772 - Endpoint to fetch pending approval notifications for logged-in user
    @GetMapping("/pending-approvals")
    public ResponseEntity<List<NotificationDTO>> getPendingApprovalNotifications(@RequestParam String userId) {
        List<NotificationDTO> notifications = notificationService.getPendingApprovalNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
}