package com.fpm.service;

import com.fpm.dto.ApprovalStatusResponse;

public interface ApprovalStatusService {
    // STORY: FPMAPP-9035 - Service interface to retrieve approval status
    ApprovalStatusResponse getApprovalStatus(String requestId);
}
