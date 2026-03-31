package com.fpm.dto;

public class ApprovalRoutingResponseDTO {

    private String nextApproverRole;
    private String nextApproverUserId;
    private String routingMessage;

    // STORY: FPMAPP-9044 - DTO for approval routing response

    public String getNextApproverRole() {
        return nextApproverRole;
    }

    public void setNextApproverRole(String nextApproverRole) {
        this.nextApproverRole = nextApproverRole;
    }

    public String getNextApproverUserId() {
        return nextApproverUserId;
    }

    public void setNextApproverUserId(String nextApproverUserId) {
        this.nextApproverUserId = nextApproverUserId;
    }

    public String getRoutingMessage() {
        return routingMessage;
    }

    public void setRoutingMessage(String routingMessage) {
        this.routingMessage = routingMessage;
    }
}