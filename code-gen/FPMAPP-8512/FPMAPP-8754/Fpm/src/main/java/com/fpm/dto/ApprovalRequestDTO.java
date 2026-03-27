package com.fpm.dto;

import java.time.LocalDateTime;

public class ApprovalRequestDTO {

    private Long id;
    private String requester;
    private String status;
    private LocalDateTime completedDate;

    // STORY: FPMAPP-8754 - DTO to represent approval request data for completed requests

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }
}