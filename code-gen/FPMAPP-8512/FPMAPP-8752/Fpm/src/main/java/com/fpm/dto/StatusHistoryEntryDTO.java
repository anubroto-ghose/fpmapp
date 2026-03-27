package com.fpm.dto;

import java.time.LocalDateTime;

public class StatusHistoryEntryDTO {

    private Long id;
    private String status;
    private String changedBy;
    private LocalDateTime timestamp;
    private String comments;

    public StatusHistoryEntryDTO(Long id, String status, String changedBy, LocalDateTime timestamp, String comments) {
        this.id = id;
        this.status = status;
        this.changedBy = changedBy;
        this.timestamp = timestamp;
        this.comments = comments;
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

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}