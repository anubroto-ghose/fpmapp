package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal requestValue;

    @Column(nullable = false)
    private String approvalStatus; // e.g., PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private String currentApproverRole; // e.g., MANAGER, DIRECTOR

    // other fields like requesterId, requestDate, etc.

    // STORY: FPMAPP-8909 - Added approvalStatus and currentApproverRole fields to track approval workflow

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRequestValue() {
        return requestValue;
    }

    public void setRequestValue(BigDecimal requestValue) {
        this.requestValue = requestValue;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getCurrentApproverRole() {
        return currentApproverRole;
    }

    public void setCurrentApproverRole(String currentApproverRole) {
        this.currentApproverRole = currentApproverRole;
    }

    // TODO: Add other necessary fields and methods
}
