package com.fpm.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApprovalRequestStatusUpdateDTO {

    private Long requestId;
    private String currentStatus;
    private List<StatusHistoryEntry> statusHistory;
    private LocalDateTime lastUpdated;

    // STORY: FPMAPP-8917 - DTO to represent approval request status update with history

    public static class StatusHistoryEntry {
        private String status;
        private LocalDateTime timestamp;
        private String updatedBy;

        public StatusHistoryEntry() {}

        public StatusHistoryEntry(String status, LocalDateTime timestamp, String updatedBy) {
            this.status = status;
            this.timestamp = timestamp;
            this.updatedBy = updatedBy;
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

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }

    public ApprovalRequestStatusUpdateDTO() {}

    public ApprovalRequestStatusUpdateDTO(Long requestId, String currentStatus, List<StatusHistoryEntry> statusHistory, LocalDateTime lastUpdated) {
        this.requestId = requestId;
        this.currentStatus = currentStatus;
        this.statusHistory = statusHistory;
        this.lastUpdated = lastUpdated;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<StatusHistoryEntry> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusHistoryEntry> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}