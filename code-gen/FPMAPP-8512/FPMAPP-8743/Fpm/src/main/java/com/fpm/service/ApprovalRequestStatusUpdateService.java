package com.fpm.service;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import java.util.List;

public interface ApprovalRequestStatusUpdateService {

    // STORY: FPMAPP-8743 - Broadcast multiple approval request status updates simultaneously
    void broadcastStatusUpdates(List<ApprovalRequestStatusUpdateDTO> updates);
}