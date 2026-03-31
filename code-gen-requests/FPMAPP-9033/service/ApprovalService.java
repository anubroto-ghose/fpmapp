package com.fpm.service;

import com.fpm.dto.ApprovalRequestDTO;
import java.util.Map;

// STORY: FPMAPP-9033 - Service interface for approval workflow processing
public interface ApprovalService {
    Map<String, String> processApproval(ApprovalRequestDTO request);
}
