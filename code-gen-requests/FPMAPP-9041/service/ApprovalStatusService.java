package com.fpm.service;

import com.fpm.dto.ApprovalStatusDTO;

public interface ApprovalStatusService {

    // STORY: FPMAPP-9041 - Fetch current approval status for a given request
    ApprovalStatusDTO getCurrentApprovalStatus(String requestId);

    // STORY: FPMAPP-9041 - Push approval status update to WebSocket subscribers
    void notifyApprovalStatusUpdate(String requestId, ApprovalStatusDTO statusDTO);

}
