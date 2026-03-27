package com.fpm.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class ApprovalRequestStatusHistoryDTO {

    private Long approvalRequestId;
    private List<StatusHistoryEntry> statusHistoryEntries = new ArrayList<>();

    // STORY: FPMAPP-8755 - DTO to represent status history with handling for incomplete/missing data

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public List<StatusHistoryEntry> getStatusHistoryEntries() {
        return statusHistoryEntries;
    }

    public void setStatusHistoryEntries(List<StatusHistoryEntry> statusHistoryEntries) {
        this.statusHistoryEntries = statusHistoryEntries;
    }

    public static class StatusHistoryEntry {
        private String status;
        private LocalDateTime timestamp;
        private String updatedBy;
        private String notes;

        // STORY: FPMAPP-8755 - Inner class to represent each status history entry with nullable fields

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

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public boolean isComplete() {
            return status != null && timestamp != null && updatedBy != null;
        }
    }

    // Helper method to check if any entry is incomplete
    public boolean hasIncompleteEntries() {
        return statusHistoryEntries.stream().anyMatch(e -> !e.isComplete());
    }

}
