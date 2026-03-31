package com.fpm.dto;

public class UploadProgressUpdateDTO {

    private String userId;
    private String uploadId;
    private int progressPercentage; // 0-100
    private String status; // e.g., IN_PROGRESS, COMPLETED, FAILED

    // STORY: FPMAPP-8913 - DTO for sending real-time upload progress updates

    public UploadProgressUpdateDTO() {
    }

    public UploadProgressUpdateDTO(String userId, String uploadId, int progressPercentage, String status) {
        this.userId = userId;
        this.uploadId = uploadId;
        this.progressPercentage = progressPercentage;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

}
