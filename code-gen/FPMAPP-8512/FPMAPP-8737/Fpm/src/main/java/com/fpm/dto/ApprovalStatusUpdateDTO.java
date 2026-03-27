package com.fpm.dto;

import java.time.LocalDateTime;

public class ApprovalStatusUpdateDTO {

    private Long requestId;
    private String status;
    private LocalDateTime lastUpdated;

    // STORY: FPMAPP-8737 - DTO to transfer approval request status updates

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
