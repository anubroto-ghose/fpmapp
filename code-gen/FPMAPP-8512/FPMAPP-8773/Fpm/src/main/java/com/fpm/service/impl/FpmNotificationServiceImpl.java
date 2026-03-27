package com.fpm.service.impl;

import com.fpm.dto.NotificationDto;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.Notification;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.NotificationRepository;
import com.fpm.service.FpmNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FpmNotificationServiceImpl implements FpmNotificationService {

    private final NotificationRepository notificationRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public FpmNotificationServiceImpl(NotificationRepository notificationRepository, ApprovalRequestRepository approvalRequestRepository) {
        this.notificationRepository = notificationRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    @Override
    // STORY: FPMAPP-8773 - Retrieve notifications for a user including pending approvals
    public List<NotificationDto> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8773 - Process approval action and update notification status
    public NotificationDto processApprovalAction(Long approvalId, Long userId, String action) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found"));

        // TODO: Validate that the userId is authorized to take action on this approval

        if (!approvalRequest.getStatus().equalsIgnoreCase("PENDING")) {
            throw new IllegalStateException("Approval request is not pending");
        }

        if ("approve" .equalsIgnoreCase(action)) {
            approvalRequest.setStatus("APPROVED");
        } else if ("reject".equalsIgnoreCase(action)) {
            approvalRequest.setStatus("REJECTED");
        } else {
            throw new IllegalArgumentException("Invalid action. Must be 'approve' or 'reject'.");
        }

        approvalRequestRepository.save(approvalRequest);

        // Update related notification
        Notification notification = notificationRepository.findByApprovalRequestIdAndUserId(approvalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found for approval request and user"));

        notification.setStatus(approvalRequest.getStatus());
        notification.setContent(generateNotificationContent(approvalRequest));
        notificationRepository.save(notification);

        return toDto(notification);
    }

    private NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setContent(notification.getContent());
        dto.setStatus(notification.getStatus());
        dto.setTimestamp(notification.getTimestamp());
        return dto;
    }

    private String generateNotificationContent(ApprovalRequest approvalRequest) {
        // STORY: FPMAPP-8773 - Generate updated notification content after approval action
        String base = "Approval request '" + approvalRequest.getTitle() + "' has been ";
        switch (approvalRequest.getStatus()) {
            case "APPROVED":
                return base + "approved.";
            case "REJECTED":
                return base + "rejected.";
            default:
                return base + "updated.";
        }
    }
}