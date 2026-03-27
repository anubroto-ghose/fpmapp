package com.fpm.service.impl;

import com.fpm.exception.PermissionDeniedException;
import com.fpm.service.AuditLogService;
import com.fpm.service.FpmApprovalService;
import com.fpm.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FpmApprovalServiceImpl implements FpmApprovalService {

    @Autowired
    private AuditLogService auditLogService;

    // STORY: FPMAPP-8766 - Reject action denied and no audit log created if user lacks approval permissions
    @Override
    public void rejectRequest(Long requestId) {
        // Check if current user has approval permissions
        if (!SecurityUtil.currentUserHasApprovalPermission()) {
            // Do NOT create audit log entry for rejection attempt
            // Throw permission denied exception
            throw new PermissionDeniedException("User lacks approval permissions to reject the request.");
        }

        // TODO: Implement actual rejection logic here
        // TODO: Create audit log entry for successful rejection
        auditLogService.logRejection(requestId, SecurityUtil.getCurrentUsername());
    }
}
