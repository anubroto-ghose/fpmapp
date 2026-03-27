package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "comments", nullable = false)
    private String comments;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // STORY: FPMAPP-8764 - Ensure audit log entry is immutable by not providing setters for id and timestamp

    public AuditLogEntry() {
    }

    public AuditLogEntry(String userId, String actionType, String comments, LocalDateTime timestamp) {
        this.userId = userId;
        this.actionType = actionType;
        this.comments = comments;
        this.timestamp = timestamp;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // No setter for timestamp to keep immutability
}