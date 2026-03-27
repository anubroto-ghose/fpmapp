package com.fpm.service.impl;

import com.fpm.dto.StatusHistoryEntryDTO;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.StatusHistoryEntry;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.StatusHistoryEntryRepository;
import com.fpm.service.FpmApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FpmApprovalRequestServiceImpl implements FpmApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private StatusHistoryEntryRepository statusHistoryEntryRepository;

    // STORY: FPMAPP-8752 - Retrieve all status history entries for a completed approval request with full data
    @Override
    public List<StatusHistoryEntryDTO> getCompleteStatusHistory(Long requestId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found: " + requestId));

        // TODO: Validate that the request is completed
        if (!"COMPLETED".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Approval request is not completed: " + requestId);
        }

        List<StatusHistoryEntry> entries = statusHistoryEntryRepository.findByApprovalRequestIdOrderByTimestampAsc(requestId);

        // TODO: Ensure entries have complete and accurate data, no placeholders
        // For now, assume repository returns complete data

        return entries.stream()
                .map(entry -> new StatusHistoryEntryDTO(
                        entry.getId(),
                        entry.getStatus(),
                        entry.getChangedBy(),
                        entry.getTimestamp(),
                        entry.getComments()
                ))
                .collect(Collectors.toList());
    }
}