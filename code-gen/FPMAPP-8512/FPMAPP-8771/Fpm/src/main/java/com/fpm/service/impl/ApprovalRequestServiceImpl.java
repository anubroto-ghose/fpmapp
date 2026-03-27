package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalRequestService;
import com.fpm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private NotificationService notificationService;

    // TODO: Inject ApprovalRequest repository

    @Override
    // STORY: FPMAPP-8771 - Submit approval request and trigger notification
    public ApprovalRequest submitApprovalRequest(ApprovalRequest approvalRequest) {
        // TODO: Persist approvalRequest to DB

        ApprovalRequest savedRequest = null; // TODO: save approvalRequest and assign

        // Notify the user who needs to approve
        Long approverUserId = savedRequest.getApproverUserId(); // TODO: get approver user id
        Long approvalRequestId = savedRequest.getId();
        String approvalRequestTitle = savedRequest.getTitle();

        notificationService.createPendingApprovalNotification(approverUserId, approvalRequestId, approvalRequestTitle);

        return savedRequest;
    }
}
