package com.fpm.service;

import com.fpm.model.ApprovalRequest;
import com.fpm.dto.ApprovalRequestUpdateDto;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8748 - Update approval request without triggering notification if status unchanged
    ApprovalRequest updateApprovalRequest(Long requestId, ApprovalRequestUpdateDto updateDto);

    // TODO: other existing methods
}
