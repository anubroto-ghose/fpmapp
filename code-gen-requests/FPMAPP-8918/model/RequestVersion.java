package com.fpm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_versions")
public class RequestVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "changed_by_user_id", nullable = false)
    private Long changedByUserId;

    @Column(name = "change_timestamp", nullable = false)
    private LocalDateTime changeTimestamp;

    @Column(name = "change_summary", columnDefinition = "TEXT", nullable = false)
    private String changeSummary; // JSON or text describing changes

    @Column(name = "request_data", columnDefinition = "LONGTEXT", nullable = false)
    private String requestData; // Serialized snapshot of the request after change

    // STORY: FPMAPP-8918 - default constructor
    public RequestVersion() {
    }

    // STORY: FPMAPP-8918 - constructor with all fields except id
    public RequestVersion(Long requestId, Integer versionNumber, Long changedByUserId, LocalDateTime changeTimestamp, String changeSummary, String requestData) {
        this.requestId = requestId;
        this.versionNumber = versionNumber;
        this.changedByUserId = changedByUserId;
        this.changeTimestamp = changeTimestamp;
        this.changeSummary = changeSummary;
        this.requestData = requestData;
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

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public LocalDateTime getChangeTimestamp() {
        return changeTimestamp;
    }

    public void setChangeTimestamp(LocalDateTime changeTimestamp) {
        this.changeTimestamp = changeTimestamp;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
}
