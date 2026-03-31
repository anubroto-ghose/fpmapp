package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delegation_logs")
public class DelegationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delegator_id", nullable = false)
    private String delegatorId;

    @Column(name = "delegatee_id", nullable = false)
    private String delegateeId;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(name = "action_result", nullable = false)
    private String actionResult;

    @Column(name = "reason")
    private String reason;

    public DelegationLog() {
    }

    public DelegationLog(String delegatorId, String delegateeId, String requestId, LocalDateTime actionTimestamp, String actionResult, String reason) {
        this.delegatorId = delegatorId;
        this.delegateeId = delegateeId;
        this.requestId = requestId;
        this.actionTimestamp = actionTimestamp;
        this.actionResult = actionResult;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public String getDelegatorId() {
        return delegatorId;
    }

    public void setDelegatorId(String delegatorId) {
        this.delegatorId = delegatorId;
    }

    public String getDelegateeId() {
        return delegateeId;
    }

    public void setDelegateeId(String delegateeId) {
        this.delegateeId = delegateeId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
