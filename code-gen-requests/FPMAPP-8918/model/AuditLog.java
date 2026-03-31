package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // e.g. APPROVAL_DECISION, DELEGATION_ACTION, REQUEST_CHANGE

    @Column(name = "action_details", columnDefinition = "TEXT", nullable = false)
    private String actionDetails;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "immutable", nullable = false)
    private boolean immutable = true; // STORY: FPMAPP-8918 - mark audit logs as immutable

    // STORY: FPMAPP-8918 - default constructor
    public AuditLog() {
    }

    // STORY: FPMAPP-8918 - constructor with all fields except id
    public AuditLog(Long requestId, Long userId, String actionType, String actionDetails, LocalDateTime timestamp) {
        this.requestId = requestId;
        this.userId = userId;
        this.actionType = actionType;
        this.actionDetails = actionDetails;
        this.timestamp = timestamp;
        this.immutable = true;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }
}
