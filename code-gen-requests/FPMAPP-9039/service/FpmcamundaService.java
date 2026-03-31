package com.fpm.service;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.util.ResponseWrapper;

public interface FpmcamundaService {

    // STORY: FPMAPP-9039 - Service method signature for processing approval with role-based routing and delegation
    ResponseWrapper processApproval(ApprovalRequestDTO approvalRequest);
}
