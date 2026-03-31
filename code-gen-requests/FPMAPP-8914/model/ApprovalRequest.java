package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double requestValue;

    @Column(nullable = false)
    private String status; // e.g., PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private String currentApproverRole; // e.g., MANAGER, DIRECTOR

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // STORY: FPMAPP-8914 - Entity to track approval requests with role-based metadata

    public ApprovalRequest() {
    }

    public ApprovalRequest(Double requestValue, String status, String requestedBy, String currentApproverRole) {
        this.requestValue = requestValue;
        this.status = status;
        this.requestedBy = requestedBy;
        this.currentApproverRole = currentApproverRole;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRequestValue() {
        return requestValue;
    }

    public void setRequestValue(Double requestValue) {
        this.requestValue = requestValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getCurrentApproverRole() {
        return currentApproverRole;
    }

    public void setCurrentApproverRole(String currentApproverRole) {
        this.currentApproverRole = currentApproverRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
