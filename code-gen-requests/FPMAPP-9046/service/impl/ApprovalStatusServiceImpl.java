package com.fpm.service.impl;

import com.fpm.dto.ApprovalStatusResponse;
import com.fpm.dto.ApprovalStatusResponse.ApprovalStage;
import com.fpm.service.ApprovalStatusService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApprovalStatusServiceImpl implements ApprovalStatusService {

    // STORY: FPMAPP-9046 - Implement business logic to fetch approval status by request ID
    @Override
    public ApprovalStatusResponse getApprovalStatusByRequestId(String requestId) {
        // TODO: Implement actual data retrieval from DB or other services
        // For now, return dummy data for demonstration

        ApprovalStatusResponse response = new ApprovalStatusResponse();
        response.setRequestId(requestId);
        response.setCurrentStatus("In Progress");

        List<ApprovalStage> stages = new ArrayList<>();

        ApprovalStage stage1 = new ApprovalStage();
        stage1.setStageName("Initial Review");
        stage1.setStatus("Approved");
        stage1.setApprover("John Doe");
        stage1.setUpdatedAt(LocalDateTime.now().minusDays(2));

        ApprovalStage stage2 = new ApprovalStage();
        stage2.setStageName("Financial Approval");
        stage2.setStatus("In Progress");
        stage2.setApprover("Jane Smith");
        stage2.setUpdatedAt(LocalDateTime.now().minusHours(3));

        stages.add(stage1);
        stages.add(stage2);

        response.setStages(stages);

        return response;
    }
}
