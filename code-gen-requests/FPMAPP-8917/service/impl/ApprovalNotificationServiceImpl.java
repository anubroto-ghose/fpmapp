package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalNotificationService;
import com.fpm.controller.ApprovalNotificationWebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalNotificationServiceImpl implements ApprovalNotificationService {

    @Autowired
    private ApprovalNotificationWebSocketController webSocketController;

    @Autowired
    private JavaMailSender mailSender;

    // STORY: FPMAPP-8917 - Notify user in real-time via WebSocket
    @Override
    @Transactional
    public void notifyUserApprovalStatusUpdate(Long userId, ApprovalRequestStatusUpdateDTO updateDTO) {
        // TODO: Add concurrency control if needed to prevent conflicts
        webSocketController.sendApprovalStatusUpdateToUser(userId, updateDTO);
    }

    // STORY: FPMAPP-8917 - Send email alert to user
    @Override
    public void sendEmailNotification(Long userId, ApprovalRequestStatusUpdateDTO updateDTO) {
        // TODO: Fetch user email by userId from user service or repository
        String userEmail = fetchUserEmailById(userId);
        if (userEmail == null || userEmail.isEmpty()) {
            // TODO: Log warning about missing email
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Approval Request Status Update");
        message.setText(buildEmailBody(updateDTO));

        mailSender.send(message);
    }

    private String fetchUserEmailById(Long userId) {
        // TODO: Implement user email retrieval logic
        return "user@example.com"; // placeholder
    }

    private String buildEmailBody(ApprovalRequestStatusUpdateDTO updateDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your approval request #").append(updateDTO.getRequestId()).append(" status has changed to: ")
          .append(updateDTO.getCurrentStatus()).append(".\n\nStatus History:\n");
        if (updateDTO.getStatusHistory() != null) {
            updateDTO.getStatusHistory().forEach(entry -> {
                sb.append("- ").append(entry.getTimestamp()).append(": ")
                  .append(entry.getStatus()).append(" (updated by ").append(entry.getUpdatedBy()).append(")\n");
            });
        }
        sb.append("\nPlease log in to the FPM application for more details.");
        return sb.toString();
    }
}