package com.fpm.dto;

import java.time.LocalDateTime;

public class StatusHistoryDTO {

    private Long id;
    private Long approvalRequestId;
    private String status;
    private String changedBy;
    private LocalDateTime changedAt;

    public StatusHistoryDTO() {
    }

    public StatusHistoryDTO(Long id, Long approvalRequestId, String status, String changedBy, LocalDateTime changedAt) {
        this.id = id;
        this.approvalRequestId = approvalRequestId;
        this.status = status;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}