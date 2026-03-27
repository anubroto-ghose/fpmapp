package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.FpmApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FpmApprovalServiceImpl implements FpmApprovalService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    // STORY: FPMAPP-8740 - Retrieve list of active approval requests with current status
    @Override
    public List<ApprovalRequestStatusUpdateDTO> getActiveApprovalRequests() {
        List<ApprovalRequest> activeRequests = approvalRequestRepository.findByStatusInProgress();
        // TODO: Map ApprovalRequest entities to ApprovalRequestStatusUpdateDTO
        return activeRequests.stream().map(req -> {
            ApprovalRequestStatusUpdateDTO dto = new ApprovalRequestStatusUpdateDTO();
            dto.setRequestId(req.getId());
            dto.setStatus(req.getStatus());
            dto.setLastUpdated(req.getLastUpdated());
            // TODO: Add other relevant fields
            return dto;
        }).collect(Collectors.toList());
    }

}
