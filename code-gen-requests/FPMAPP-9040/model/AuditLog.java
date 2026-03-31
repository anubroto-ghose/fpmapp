package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_action_type", columnList = "actionType"),
    @Index(name = "idx_audit_user_id", columnList = "userId"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String actionType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String actionDetails;

    @Column(columnDefinition = "JSON")
    private String delegationInfo; // JSON string storing delegation details

    @Column(nullable = false)
    private boolean overrideFlag;

    // STORY: FPMAPP-9040 - Entity representing audit log entries for approval, rejection, delegation actions

    public AuditLog() {
    }

    public AuditLog(String userId, String actionType, LocalDateTime timestamp, String actionDetails, String delegationInfo, boolean overrideFlag) {
        this.userId = userId;
        this.actionType = actionType;
        this.timestamp = timestamp;
        this.actionDetails = actionDetails;
        this.delegationInfo = delegationInfo;
        this.overrideFlag = overrideFlag;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    public String getDelegationInfo() {
        return delegationInfo;
    }

    public void setDelegationInfo(String delegationInfo) {
        this.delegationInfo = delegationInfo;
    }

    public boolean isOverrideFlag() {
        return overrideFlag;
    }

    public void setOverrideFlag(boolean overrideFlag) {
        this.overrideFlag = overrideFlag;
    }
}
