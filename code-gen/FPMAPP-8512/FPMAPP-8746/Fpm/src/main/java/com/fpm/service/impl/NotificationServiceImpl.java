package com.fpm.service.impl;

import com.fpm.dto.NotificationDTO;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // STORY: FPMAPP-8746 - Trigger notification immediately on rejection status change
    @Override
    @Async // Ensure immediate async processing to avoid delay
    public void notifyRejectionStatusChange(ApprovalRequest approvalRequest, String approverId) {
        // TODO: Fetch requester email and notification preferences
        String requesterEmail = approvalRequest.getRequesterEmail();

        // Compose notification content
        String subject = "Approval Request Rejected";
        String body = String.format(
            "Your approval request (ID: %s) was rejected by approver %s at %s.",
            approvalRequest.getId(),
            approverId,
            Instant.now().toString()
        );

        // Send email notification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(requesterEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);

        // TODO: Push notification to UI notification system (e.g. WebSocket, message queue)
        // This should include status, timestamp, and approver details

        // TODO: Implement idempotency or deduplication if needed to avoid duplicate notifications
    }
}