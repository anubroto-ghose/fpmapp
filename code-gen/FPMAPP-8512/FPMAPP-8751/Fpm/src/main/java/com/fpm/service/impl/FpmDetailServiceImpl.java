package com.fpm.service.impl;

import com.fpm.dto.StatusHistoryDTO;
import com.fpm.repository.StatusHistoryRepository;
import com.fpm.service.FpmDetailService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FpmDetailServiceImpl implements FpmDetailService {

    private final StatusHistoryRepository statusHistoryRepository;

    public FpmDetailServiceImpl(StatusHistoryRepository statusHistoryRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
    }

    // STORY: FPMAPP-8751 - Return empty list if no status history found to handle missing data gracefully
    @Override
    public List<StatusHistoryDTO> getStatusHistoryByApprovalRequestId(Long requestId) {
        List<StatusHistoryDTO> statusHistory = statusHistoryRepository.findByApprovalRequestId(requestId);
        if (statusHistory == null) {
            // TODO: Log missing data scenario if needed
            return Collections.emptyList();
        }
        return statusHistory;
    }
}