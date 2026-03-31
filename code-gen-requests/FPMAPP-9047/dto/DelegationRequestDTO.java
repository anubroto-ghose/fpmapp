package com.fpm.dto;

import jakarta.validation.constraints.NotBlank;

public class DelegationRequestDTO {

    @NotBlank
    private String delegatorId;

    @NotBlank
    private String delegateeId;

    @NotBlank
    private String requestId;

    public DelegationRequestDTO() {
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
}
