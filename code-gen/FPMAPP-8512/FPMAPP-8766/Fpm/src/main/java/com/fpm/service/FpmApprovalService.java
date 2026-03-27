package com.fpm.service;

public interface FpmApprovalService {

    // STORY: FPMAPP-8766 - Reject action denied and no audit log created if user lacks approval permissions
    void rejectRequest(Long requestId);
}
