package com.fpm.service;

import com.fpm.exception.DelegationException;

public interface DelegationService {

    // STORY: FPMAPP-8763 - Attempt delegation and ensure audit log only on success
    void delegateApprovalRights(String fromUserId, String toUserId) throws DelegationException;

}