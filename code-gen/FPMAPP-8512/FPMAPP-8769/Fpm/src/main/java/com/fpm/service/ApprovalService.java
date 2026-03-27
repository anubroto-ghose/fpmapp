package com.fpm.service;

import com.fpm.exception.PermissionDeniedException;
import com.fpm.model.ApprovalRequest;

public interface ApprovalService {

    // STORY: FPMAPP-8769 - Attempt approval action with permission check
    void approveRequest(ApprovalRequest request, String username) throws PermissionDeniedException;
}