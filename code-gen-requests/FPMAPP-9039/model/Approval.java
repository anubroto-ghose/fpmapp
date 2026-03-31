package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approval_id", unique = true, nullable = false)
    private String approvalId;

    @Column(name = "approval_role")
    private String approvalRole;

    @Column(name = "delegation_flag")
    private Boolean delegationFlag = false;

    @Column(name = "financial_threshold", precision = 19, scale = 4)
    private BigDecimal financialThreshold;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Additional fields like user_role, delegation_info can be stored in audit logs or separate tables

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public String getApprovalRole() {
        return approvalRole;
    }

    public void setApprovalRole(String approvalRole) {
        this.approvalRole = approvalRole;
    }

    public Boolean getDelegationFlag() {
        return delegationFlag;
    }

    public void setDelegationFlag(Boolean delegationFlag) {
        this.delegationFlag = delegationFlag;
    }

    public BigDecimal getFinancialThreshold() {
        return financialThreshold;
    }

    public void setFinancialThreshold(BigDecimal financialThreshold) {
        this.financialThreshold = financialThreshold;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
