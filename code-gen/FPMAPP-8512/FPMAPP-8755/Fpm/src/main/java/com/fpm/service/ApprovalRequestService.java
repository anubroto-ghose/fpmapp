package com.fpm.service;

import com.fpm.dto.ApprovalRequestStatusHistoryDTO;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8755 - Service method to fetch status history with graceful handling of incomplete data
    ApprovalRequestStatusHistoryDTO getStatusHistoryByApprovalRequestId(Long approvalRequestId);

}
