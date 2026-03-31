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

    @Column(name = "action_type", nullable = false)
    private String actionType; // e.g. APPROVAL, REJECTION, DELEGATION, OVERRIDE

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(name = "immutable", nullable = false)
    private boolean immutable = true; // STORY: FPMAPP-8911 - Ensure immutability flag

    // TODO: Implement encryption for sensitive fields if required

    public AuditLog() {
    }

    public AuditLog(Long approvalRequestId, String userId, String actionType, String comments, LocalDateTime actionTimestamp) {
        this.approvalRequestId = approvalRequestId;
        this.userId = userId;
        this.actionType = actionType;
        this.comments = comments;
        this.actionTimestamp = actionTimestamp;
        this.immutable = true; // STORY: FPMAPP-8911 - Mark log as immutable on creation
    }

    public Long getId() {
        return id;
    }

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }
}
