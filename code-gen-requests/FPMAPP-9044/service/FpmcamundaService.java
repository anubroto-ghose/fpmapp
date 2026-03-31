package com.fpm.service;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.dto.ApprovalRoutingResponseDTO;

public interface FpmcamundaService {

    // STORY: FPMAPP-9044 - Route approval request based on role and financial thresholds
    ApprovalRoutingResponseDTO routeApproval(ApprovalRequestDTO approvalRequestDTO);

}