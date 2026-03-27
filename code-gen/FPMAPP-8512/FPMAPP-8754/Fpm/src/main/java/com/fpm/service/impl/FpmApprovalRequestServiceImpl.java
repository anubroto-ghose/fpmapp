package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.FpmApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FpmApprovalRequestServiceImpl implements FpmApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    // STORY: FPMAPP-8754 - Return empty list if no completed approval requests found
    @Override
    public List<ApprovalRequestDTO> getCompletedApprovalRequests() {
        // TODO: Implement query to fetch completed approval requests from DB
        var completedEntities = approvalRequestRepository.findByStatus("COMPLETED");
        if (completedEntities == null || completedEntities.isEmpty()) {
            return List.of();
        }
        return completedEntities.stream()
                .map(entity -> {
                    ApprovalRequestDTO dto = new ApprovalRequestDTO();
                    dto.setId(entity.getId());
                    dto.setRequester(entity.getRequester());
                    dto.setStatus(entity.getStatus());
                    dto.setCompletedDate(entity.getCompletedDate());
                    // TODO: Map other relevant fields
                    return dto;
                })
                .collect(Collectors.toList());
    }
}