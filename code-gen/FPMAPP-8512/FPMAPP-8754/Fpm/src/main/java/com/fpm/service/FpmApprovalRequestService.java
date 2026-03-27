package com.fpm.service;

import com.fpm.dto.ApprovalRequestDTO;

import java.util.List;

public interface FpmApprovalRequestService {

    // STORY: FPMAPP-8754 - Service method to fetch completed approval requests
    List<ApprovalRequestDTO> getCompletedApprovalRequests();
}