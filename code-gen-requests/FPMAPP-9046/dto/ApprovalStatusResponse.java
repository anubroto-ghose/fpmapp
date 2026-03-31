package com.fpm.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApprovalStatusResponse {

    private String requestId;
    private String currentStatus;
    private List<ApprovalStage> stages;

    public static class ApprovalStage {
        private String stageName;
        private String status;
        private String approver;
        private LocalDateTime updatedAt;

        // Getters and Setters
        public String getStageName() {
            return stageName;
        }

        public void setStageName(String stageName) {
            this.stageName = stageName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getApprover() {
            return approver;
        }

        public void setApprover(String approver) {
            this.approver = approver;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<ApprovalStage> getStages() {
        return stages;
    }

    public void setStages(List<ApprovalStage> stages) {
        this.stages = stages;
    }
}
