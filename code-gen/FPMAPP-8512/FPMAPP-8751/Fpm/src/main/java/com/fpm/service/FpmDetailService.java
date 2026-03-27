package com.fpm.service;

import com.fpm.dto.StatusHistoryDTO;

import java.util.List;

public interface FpmDetailService {

    // STORY: FPMAPP-8751 - Service method to retrieve status history by approval request id
    List<StatusHistoryDTO> getStatusHistoryByApprovalRequestId(Long requestId);
}