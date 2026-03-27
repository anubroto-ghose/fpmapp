package com.fpm.service;

import com.fpm.dto.ApprovalStatusUpdateDTO;

import java.util.List;

public interface FpmApprovalService {

    // STORY: FPMAPP-8737 - Retrieve all current approval statuses for UI synchronization
    List<ApprovalStatusUpdateDTO> getAllCurrentApprovalStatuses();

}
