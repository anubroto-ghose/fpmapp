
// Additional method added to ApprovalServiceImpl for fetching approval request by id

package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import java.util.Optional;

public class ApprovalServiceImpl {

    // STORY: FPMAPP-8909 - Fetch approval request by id
    public Optional<ApprovalRequest> getApprovalRequestById(Long id) {
        return approvalRequestRepository.findById(id);
    }
}
