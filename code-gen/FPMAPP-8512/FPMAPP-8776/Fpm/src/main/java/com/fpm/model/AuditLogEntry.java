package com.fpm.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "action_type", nullable = false, updatable = false)
    private String actionType;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @Column(name = "comments", length = 1000, updatable = false)
    private String comments;

    // STORY: FPMAPP-8776 - Audit log entry immutable fields and delegation action details

    protected AuditLogEntry() {
        // JPA
    }

    public AuditLogEntry(String userId, String actionType, Instant timestamp, String comments) {
        this.userId = userId;
        this.actionType = actionType;
        this.timestamp = timestamp;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getActionType() {
        return actionType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getComments() {
        return comments;
    }

    // No setters to ensure immutability after creation
}