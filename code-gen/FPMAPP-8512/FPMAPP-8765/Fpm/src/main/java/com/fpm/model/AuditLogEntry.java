package com.fpm.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String actionType;

    @Column(length = 1000)
    private String comments;

    // STORY: FPMAPP-8765 - Audit log entry is immutable, no setters for fields after creation

    protected AuditLogEntry() {
        // JPA
    }

    public AuditLogEntry(String userId, Instant timestamp, String actionType, String comments) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getActionType() {
        return actionType;
    }

    public String getComments() {
        return comments;
    }

    // No setters to ensure immutability
}