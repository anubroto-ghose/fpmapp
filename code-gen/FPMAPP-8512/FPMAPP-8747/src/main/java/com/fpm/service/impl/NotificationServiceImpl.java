package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // STORY: FPMAPP-8747 - Send notification immediately on approval status change
    @Override
    public void notifyApprovalStatusChange(ApprovalRequest approvalRequest) {
        // TODO: Implement UI notification push mechanism if applicable

        // Send email notification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(approvalRequest.getRequesterEmail());
        message.setSubject("Approval Status Updated: " + approvalRequest.getStatus());

        String timestamp = approvalRequest.getStatusChangedAt() != null ?
                approvalRequest.getStatusChangedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A";

        String text = String.format(
                "Dear User,\n\nYour approval request status has been updated to '%s'.\n" +
                "Timestamp: %s\n" +
                "Approved by: %s\n\nPlease review the updated status in your notification area or email.\n\nRegards,\nFPMTools Team",
                approvalRequest.getStatus(), timestamp, approvalRequest.getApproverName());

        message.setText(text);

        mailSender.send(message);
    }
}