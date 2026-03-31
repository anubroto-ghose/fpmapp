package com.fpm.dto;

public class UploadProgressUpdateDTO {

    private String uploadId;
    private int progressPercentage;
    private String status; // e.g., "IN_PROGRESS", "COMPLETED", "FAILED"
    private String message;

    // STORY: FPMAPP-8913 - DTO for sending real-time upload progress updates

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}