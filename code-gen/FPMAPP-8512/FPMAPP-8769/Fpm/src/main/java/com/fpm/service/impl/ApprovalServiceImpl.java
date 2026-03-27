package com.fpm.service.impl;

import com.fpm.exception.PermissionDeniedException;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalService;
import com.fpm.service.AuditLogService;
import com.fpm.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AuditLogService auditLogService;

    // STORY: FPMAPP-8769 - Attempt approval action with permission check
    @Override
    public void approveRequest(ApprovalRequest request, String username) throws PermissionDeniedException {
        boolean hasPermission = permissionService.hasApprovalPermission(username);
        if (!hasPermission) {
            // Do NOT log audit entry for denied approval
            throw new PermissionDeniedException("User lacks approval permissions.");
        }

        // TODO: Implement actual approval logic here

        // Log audit entry for successful approval
        auditLogService.logApprovalAction(username, request.getId());
    }
}