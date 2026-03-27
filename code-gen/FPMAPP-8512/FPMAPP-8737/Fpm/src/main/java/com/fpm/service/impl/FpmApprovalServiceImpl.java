package com.fpm.service.impl;

import com.fpm.dto.ApprovalStatusUpdateDTO;
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

    // STORY: FPMAPP-8737 - Implementation to fetch all current approval statuses for UI synchronization
    @Override
    public List<ApprovalStatusUpdateDTO> getAllCurrentApprovalStatuses() {
        // TODO: Fetch all active approval requests and map to DTOs
        return approvalRequestRepository.findAllActive()
                .stream()
                .map(approvalRequest -> {
                    ApprovalStatusUpdateDTO dto = new ApprovalStatusUpdateDTO();
                    dto.setRequestId(approvalRequest.getId());
                    dto.setStatus(approvalRequest.getStatus());
                    dto.setLastUpdated(approvalRequest.getLastUpdated());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
