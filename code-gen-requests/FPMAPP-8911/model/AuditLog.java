package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approval_request_id", nullable = false)
    private Long approvalRequestId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // e.g. APPROVAL, REJECTION, DELEGATION, OVERRIDE

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(name = "encrypted", nullable = false)
    private boolean encrypted = false; // TODO: Implement encryption logic if needed

    // STORY: FPMAPP-8911 - AuditLog entity to store immutable audit trail entries

    public AuditLog() {
    }

    public AuditLog(Long approvalRequestId, String userId, String actionType, String comments, LocalDateTime actionTimestamp) {
        this.approvalRequestId = approvalRequestId;
        this.userId = userId;
        this.actionType = actionType;
        this.comments = comments;
        this.actionTimestamp = actionTimestamp;
        this.encrypted = false; // TODO: Encrypt comments if required
    }

    public Long getId() {
        return id;
    }

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getComments() {
        return comments;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    // No setters for immutability except for JPA
    protected void setId(Long id) {
        this.id = id;
    }

    protected void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    protected void setUserId(String userId) {
        this.userId = userId;
    }

    protected void setActionType(String actionType) {
        this.actionType = actionType;
    }

    protected void setComments(String comments) {
        this.comments = comments;
    }

    protected void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    protected void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
}
