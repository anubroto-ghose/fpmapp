package com.fpm.service;

import com.fpm.dto.StatusHistoryEntryDTO;

import java.util.List;

public interface FpmApprovalRequestService {

    // STORY: FPMAPP-8752 - Service method to get complete status history for a completed approval request
    List<StatusHistoryEntryDTO> getCompleteStatusHistory(Long requestId);
}