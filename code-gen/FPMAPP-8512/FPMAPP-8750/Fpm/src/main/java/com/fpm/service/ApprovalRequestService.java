package com.fpm.service;

import com.fpm.dto.StatusHistoryDTO;
import java.util.List;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8750 - Retrieve status history for a completed approval request with placeholders for missing data
    List<StatusHistoryDTO> getStatusHistoryByApprovalRequestId(Long approvalRequestId);
}