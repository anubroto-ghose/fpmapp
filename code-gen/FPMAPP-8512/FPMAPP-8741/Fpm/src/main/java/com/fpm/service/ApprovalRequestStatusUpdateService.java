package com.fpm.service;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;

public interface ApprovalRequestStatusUpdateService {

    // STORY: FPMAPP-8741 - Method to publish approval request status update to WebSocket topic
    void publishStatusUpdate(ApprovalRequestStatusUpdateDTO statusUpdate);
}
