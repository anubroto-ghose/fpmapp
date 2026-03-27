package com.fpm.service;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;

import java.util.List;

public interface FpmApprovalService {

    // STORY: FPMAPP-8740 - Retrieve list of active approval requests with current status
    List<ApprovalRequestStatusUpdateDTO> getActiveApprovalRequests();

}
