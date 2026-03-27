package com.fpm.service;

import com.fpm.dto.NotificationDTO;
import com.fpm.model.ApprovalRequest;

public interface NotificationService {

    // STORY: FPMAPP-8746 - Trigger notification immediately on rejection status change
    void notifyRejectionStatusChange(ApprovalRequest approvalRequest, String approverId);

}