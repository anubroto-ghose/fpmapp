package com.fpm.dto;

import java.time.LocalDateTime;

public class StatusHistoryDTO {
    private Long id;
    private String status;
    private String updatedBy;
    private LocalDateTime updatedAt;

    // STORY: FPMAPP-8750 - DTO to represent status history with possible incomplete data

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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}