package com.fpm.util;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;

@Component
public class NotificationUtil {

    // STORY: FPMAPP-9039 - Send email and in-app notifications to approvers with role, amount, and delegation info
    public void sendApprovalNotification(String approvalRole, String approvalId, BigDecimal amount, boolean delegationFlag, Map<String, Object> delegationInfo) {
        // TODO: Implement email and in-app notification logic
        // Example: Log notification details
        String notificationMsg = String.format(
                "Notification sent to role: %s for ApprovalId: %s, Amount: %s, Delegation: %s, DelegationInfo: %s",
                approvalRole, approvalId, amount.toPlainString(), delegationFlag, delegationInfo);
        System.out.println(notificationMsg);
    }
}
