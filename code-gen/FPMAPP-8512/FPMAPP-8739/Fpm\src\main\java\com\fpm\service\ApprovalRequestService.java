package com.fpm.service;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import java.util.List;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8739 - method to fetch approval requests without real-time updates if websocket disconnected
    List<ApprovalRequestStatusUpdateDTO> getApprovalRequestsForUser(Long userId, boolean isWebSocketConnected);

    // TODO: other existing service methods
}
