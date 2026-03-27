package com.fpm.service;

public interface AuditLogService {

    // STORY: FPMAPP-8763 - Log delegation action only on successful delegation
    void logDelegationAction(String fromUserId, String toUserId);

}