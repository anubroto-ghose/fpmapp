package com.fpm.service.impl;

import com.fpm.dto.ApprovalStatusHistoryDTO;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.ApprovalStatusHistory;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.ApprovalStatusHistoryRepository;
import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ApprovalStatusHistoryRepository approvalStatusHistoryRepository;

    // STORY: FPMAPP-8753 - Retrieve full status history for a completed approval request
    @Override
    public List<ApprovalStatusHistoryDTO> getFullStatusHistory(Long approvalRequestId) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found with id: " + approvalRequestId));

        // TODO: Validate that the approval request is completed
        if (!"COMPLETED".equalsIgnoreCase(approvalRequest.getStatus())) {
            throw new IllegalStateException("Approval request is not completed");
        }

        List<ApprovalStatusHistory> historyList = approvalStatusHistoryRepository.findByApprovalRequestIdOrderByTimestampAsc(approvalRequestId);

        return historyList.stream()
                .map(h -> new ApprovalStatusHistoryDTO(
                        h.getId(),
                        h.getStatus(),
                        h.getTimestamp(),
                        h.getApprover() != null ? h.getApprover().getFullName() : null,
                        h.getApprover() != null ? h.getApprover().getRole() : null
                ))
                .collect(Collectors.toList());
    }
}
