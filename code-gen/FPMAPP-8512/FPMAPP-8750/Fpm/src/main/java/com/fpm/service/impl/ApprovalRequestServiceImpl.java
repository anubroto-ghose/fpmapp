package com.fpm.service.impl;

import com.fpm.dto.StatusHistoryDTO;
import com.fpm.service.ApprovalRequestService;
import com.fpm.repository.StatusHistoryRepository;
import com.fpm.model.StatusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private StatusHistoryRepository statusHistoryRepository;

    // STORY: FPMAPP-8750 - Implementation to fetch status history with placeholders for missing data
    @Override
    public List<StatusHistoryDTO> getStatusHistoryByApprovalRequestId(Long approvalRequestId) {
        List<StatusHistory> histories = statusHistoryRepository.findByApprovalRequestIdOrderByUpdatedAtAsc(approvalRequestId);

        // TODO: Confirm repository method returns empty list if no data

        return histories.stream().map(history -> {
            StatusHistoryDTO dto = new StatusHistoryDTO();
            dto.setId(history.getId());

            // Use placeholder if status is null or empty
            if (history.getStatus() == null || history.getStatus().isBlank()) {
                dto.setStatus("[Status not available]");
            } else {
                dto.setStatus(history.getStatus());
            }

            // Use placeholder if updatedBy is null or empty
            if (history.getUpdatedBy() == null || history.getUpdatedBy().isBlank()) {
                dto.setUpdatedBy("[Unknown user]");
            } else {
                dto.setUpdatedBy(history.getUpdatedBy());
            }

            // Use placeholder date if updatedAt is null
            if (history.getUpdatedAt() == null) {
                // TODO: Decide on placeholder date or null
                dto.setUpdatedAt(null); // UI should handle null date gracefully
            } else {
                dto.setUpdatedAt(history.getUpdatedAt());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}