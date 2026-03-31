package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_records", indexes = {
    @Index(name = "idx_approval_role", columnList = "approvalRole"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_delegated_to_user_id", columnList = "delegatedToUserId")
})
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String approvalRole;

    private BigDecimal financialThreshold;

    private Boolean delegationFlag;

    private Long delegatedToUserId;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private ApprovalActionType actionType;

    private Long userId; // The user who performed the action

    private BigDecimal financialAmount;

    private String comments;

    // STORY: FPMAPP-9033 - Approval record entity with new columns

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApprovalRole() {
        return approvalRole;
    }

    public void setApprovalRole(String approvalRole) {
        this.approvalRole = approvalRole;
    }

    public BigDecimal getFinancialThreshold() {
        return financialThreshold;
    }

    public void setFinancialThreshold(BigDecimal financialThreshold) {
        this.financialThreshold = financialThreshold;
    }

    public Boolean getDelegationFlag() {
        return delegationFlag;
    }

    public void setDelegationFlag(Boolean delegationFlag) {
        this.delegationFlag = delegationFlag;
    }

    public Long getDelegatedToUserId() {
        return delegatedToUserId;
    }

    public void setDelegatedToUserId(Long delegatedToUserId) {
        this.delegatedToUserId = delegatedToUserId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ApprovalActionType getActionType() {
        return actionType;
    }

    public void setActionType(ApprovalActionType actionType) {
        this.actionType = actionType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getFinancialAmount() {
        return financialAmount;
    }

    public void setFinancialAmount(BigDecimal financialAmount) {
        this.financialAmount = financialAmount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
