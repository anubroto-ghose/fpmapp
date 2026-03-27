package com.fpm.service;

import com.fpm.dto.ApprovalStatusHistoryDTO;
import java.util.List;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8753 - Retrieve full status history for a completed approval request
    List<ApprovalStatusHistoryDTO> getFullStatusHistory(Long approvalRequestId);
}
