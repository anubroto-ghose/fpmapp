package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_trail_logs", indexes = {
    @Index(name = "idx_action_type", columnList = "action_type"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class AuditTrailLog {

    public enum ActionType {
        APPROVAL,
        REJECTION,
        DELEGATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private ActionType actionType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "delegated_from_user_id")
    private Long delegatedFromUserId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "financial_threshold", precision = 19, scale = 4)
    private BigDecimal financialThreshold;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // e.g. DEALSHEET, STAFFING, TRAVEL

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // JSON or stringified delegationDetails or other info

    // STORY: FPMAPP-9034 - Audit trail log entity for immutable audit records

    public AuditTrailLog() {
    }

    public AuditTrailLog(ActionType actionType, Long userId, Long delegatedFromUserId, LocalDateTime timestamp,
                         BigDecimal financialThreshold, String entityType, Long entityId, String details) {
        this.actionType = actionType;
        this.userId = userId;
        this.delegatedFromUserId = delegatedFromUserId;
        this.timestamp = timestamp;
        this.financialThreshold = financialThreshold;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDelegatedFromUserId() {
        return delegatedFromUserId;
    }

    public void setDelegatedFromUserId(Long delegatedFromUserId) {
        this.delegatedFromUserId = delegatedFromUserId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getFinancialThreshold() {
        return financialThreshold;
    }

    public void setFinancialThreshold(BigDecimal financialThreshold) {
        this.financialThreshold = financialThreshold;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
