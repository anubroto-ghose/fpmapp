package com.fpm.service.impl;

import com.fpm.dto.ApprovalStatusResponse;
import com.fpm.service.ApprovalStatusService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApprovalStatusServiceImpl implements ApprovalStatusService {

    // STORY: FPMAPP-9035 - Implementation of approval status retrieval service
    @Override
    public ApprovalStatusResponse getApprovalStatus(String requestId) {
        // TODO: Implement actual retrieval logic from DB or cache
        // For now, return dummy data for demonstration

        String status = "Pending"; // e.g. "Pending", "Approved", "Rejected"
        String currentApproverRole = "Financial Analyst";

        Map<String, Object> delegationInfo = new HashMap<>();
        delegationInfo.put("delegatedTo", "John Doe");
        delegationInfo.put("delegationStart", "2024-06-01T09:00:00Z");
        delegationInfo.put("delegationEnd", "2024-06-10T18:00:00Z");

        return new ApprovalStatusResponse(status, currentApproverRole, delegationInfo);
    }
}
