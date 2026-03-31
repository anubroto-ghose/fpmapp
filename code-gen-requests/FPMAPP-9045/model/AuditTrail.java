package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_trails")
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "action_type", nullable = false)
    private String actionType; // e.g. APPROVAL, REJECTION, DELEGATION

    @Column(name = "action_details", length = 1000)
    private String actionDetails;

    @Column(name = "delegated_to_role")
    private String delegatedToRole; // nullable, only for delegation actions

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AuditTrail() {
    }

    public AuditTrail(String requestId, String userId, String actionType, String actionDetails, String delegatedToRole, LocalDateTime timestamp) {
        this.requestId = requestId;
        this.userId = userId;
        this.actionType = actionType;
        this.actionDetails = actionDetails;
        this.delegatedToRole = delegatedToRole;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    public String getDelegatedToRole() {
        return delegatedToRole;
    }

    public void setDelegatedToRole(String delegatedToRole) {
        this.delegatedToRole = delegatedToRole;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
