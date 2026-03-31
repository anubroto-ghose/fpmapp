package com.fpm.util;

import org.springframework.stereotype.Component;

// STORY: FPMAPP-9033 - Utility to send notifications on approval actions
@Component
public class NotificationUtil {

    // STORY: FPMAPP-9033 - Send notifications via email and in-app messages
    public void sendApprovalNotification(String userRole, String routingDecision, Long delegatedToUserId) {
        // TODO: Implement SMTP email sending and in-app notification logic
        // For demo, just log or simulate notification
        String targetUser = delegatedToUserId != null ? delegatedToUserId.toString() : userRole;
        System.out.println("Notification sent to " + targetUser + ": Approval action " + routingDecision);
    }
}
