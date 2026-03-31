package com.fpm.service;

import com.fpm.dto.ApprovalStatusResponse;

public interface ApprovalStatusService {

    // STORY: FPMAPP-9046 - Provide current approval status for a given request
    ApprovalStatusResponse getApprovalStatusByRequestId(String requestId);

}
