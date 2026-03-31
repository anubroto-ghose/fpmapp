package com.fpm.service;

import com.fpm.dto.ApprovalStatusUpdateDTO;

public interface NotificationService {

    // STORY: FPMAPP-8917 - Send approval status update notifications (email + in-app)
    void notifyApprovalStatusUpdate(ApprovalStatusUpdateDTO update);
}