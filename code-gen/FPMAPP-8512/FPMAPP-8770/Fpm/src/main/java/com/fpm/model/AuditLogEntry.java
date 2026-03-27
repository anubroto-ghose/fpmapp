package com.fpm.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false, updatable = false)
    private String actionType;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private String details;

    // STORY: FPMAPP-8770 - Audit log entry is immutable after creation

    protected AuditLogEntry() {
        // JPA
    }

    public AuditLogEntry(String userId, String actionType, Instant timestamp, String details) {
        this.userId = userId;
        this.actionType = actionType;
        this.timestamp = timestamp;
        this.details = details;
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

    public String getDetails() {
        return details;
    }

    // No setters to ensure immutability after creation
}
