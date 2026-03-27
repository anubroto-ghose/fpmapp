package com.fpm.dto;

import java.time.LocalDateTime;

public class ApprovalStatusHistoryDTO {
    private Long id;
    private String status;
    private LocalDateTime timestamp;
    private String approverName;
    private String approverRole;

    public ApprovalStatusHistoryDTO() {
    }

    public ApprovalStatusHistoryDTO(Long id, String status, LocalDateTime timestamp, String approverName, String approverRole) {
        this.id = id;
        this.status = status;
        this.timestamp = timestamp;
        this.approverName = approverName;
        this.approverRole = approverRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }
}
