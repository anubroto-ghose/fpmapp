package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalRequestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    // STORY: FPMAPP-8739 - fetch approval requests with websocket connection status awareness
    @Override
    public List<ApprovalRequestStatusUpdateDTO> getApprovalRequestsForUser(Long userId, boolean isWebSocketConnected) {
        // TODO: fetch approval requests from DB for the user
        List<ApprovalRequestStatusUpdateDTO> approvalRequests = new ArrayList<>();

        // TODO: populate approvalRequests list with actual data

        // If websocket is disconnected, mark realTimeConnectionActive as false
        for (ApprovalRequestStatusUpdateDTO dto : approvalRequests) {
            dto.setRealTimeConnectionActive(isWebSocketConnected);
        }

        return approvalRequests;
    }

    // TODO: other existing service methods
}
