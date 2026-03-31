package com.fpm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpm.dto.ApprovalStatusUpdateDTO;
import com.fpm.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // STORY: FPMAPP-8917 - Send approval status update notifications (email + in-app) with concurrency control
    @Override
    @Transactional
    public void notifyApprovalStatusUpdate(ApprovalStatusUpdateDTO update) {
        // Send in-app notification via WebSocket
        messagingTemplate.convertAndSendToUser(
            update.getRequesterUsername(), "/queue/approval-status", update);

        // Send email notification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(getEmailForUser(update.getRequesterUsername()));
        message.setSubject("Approval Request Status Update - Request ID: " + update.getRequestId());
        message.setText(buildEmailBody(update));

        // TODO: Add retry or async email sending if needed
        mailSender.send(message);
    }

    private String getEmailForUser(String username) {
        // TODO: Implement user email lookup from user service or DB
        return username + "@example.com"; // placeholder
    }

    private String buildEmailBody(ApprovalStatusUpdateDTO update) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(update.getRequesterUsername()).append(",\n\n");
        sb.append("Your approval request (ID: ").append(update.getRequestId()).append(") status has changed.\n");
        sb.append("Previous Status: ").append(update.getPreviousStatus()).append("\n");
        sb.append("Current Status: ").append(update.getCurrentStatus()).append("\n");
        sb.append("Updated By: ").append(update.getUpdatedBy()).append("\n");
        sb.append("Timestamp: ").append(update.getUpdateTimestamp()).append("\n\n");
        if (update.getMessage() != null && !update.getMessage().isEmpty()) {
            sb.append("Message: ").append(update.getMessage()).append("\n\n");
        }
        sb.append("Thank you,\nFPMTools Team");
        return sb.toString();
    }
}