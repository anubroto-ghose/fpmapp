package com.fpm.service.impl;

import com.fpm.dto.NotificationDTO;
import com.fpm.service.NotificationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    // TODO: Inject repository or messaging system to persist/send notifications

    @Override
    // STORY: FPMAPP-8771 - Create notification for pending approval request
    public void createPendingApprovalNotification(Long userId, Long approvalRequestId, String approvalRequestTitle) {
        // TODO: Persist notification to DB or send via messaging queue
        String message = "You have a pending approval request: '" + approvalRequestTitle + "'.";
        String actionLink = "/approvals/" + approvalRequestId + "/review"; // Link to approval details in UI

        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setActionLink(actionLink);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // TODO: Save notification entity or send notification event
    }
}
