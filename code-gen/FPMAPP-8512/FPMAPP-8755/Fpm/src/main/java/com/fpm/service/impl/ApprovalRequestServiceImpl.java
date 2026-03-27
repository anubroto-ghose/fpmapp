package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestStatusHistoryDTO;
import com.fpm.dto.ApprovalRequestStatusHistoryDTO.StatusHistoryEntry;
import com.fpm.service.ApprovalRequestService;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.StatusHistory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    // STORY: FPMAPP-8755 - Implementation to fetch status history and handle incomplete/missing data gracefully
    @Override
    public ApprovalRequestStatusHistoryDTO getStatusHistoryByApprovalRequestId(Long approvalRequestId) {
        ApprovalRequestStatusHistoryDTO dto = new ApprovalRequestStatusHistoryDTO();
        dto.setApprovalRequestId(approvalRequestId);

        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId).orElse(null);
        if (approvalRequest == null) {
            // TODO: Consider throwing a custom NotFoundException or return empty DTO
            return dto;
        }

        List<StatusHistory> histories = approvalRequest.getStatusHistories();
        if (histories == null || histories.isEmpty()) {
            // No status history available, return empty list
            return dto;
        }

        List<StatusHistoryEntry> entries = histories.stream().map(history -> {
            StatusHistoryEntry entry = new StatusHistoryEntry();

            // Gracefully handle missing or incomplete data
            entry.setStatus(history.getStatus() != null ? history.getStatus() : "[Status Missing]");
            entry.setTimestamp(history.getTimestamp()); // can be null
            entry.setUpdatedBy(history.getUpdatedBy() != null ? history.getUpdatedBy() : "[User Unknown]");
            entry.setNotes(history.getNotes()); // optional

            return entry;
        }).collect(Collectors.toList());

        dto.setStatusHistoryEntries(entries);
        return dto;
    }

}
