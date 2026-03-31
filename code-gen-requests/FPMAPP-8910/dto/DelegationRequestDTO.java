package com.fpm.dto;

import com.fpm.model.User;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class DelegationRequestDTO {

    @NotNull(message = "Delegate user must be provided")
    private User delegateUser;

    @NotNull(message = "Permissions must be provided")
    private String permissions; // JSON or CSV string representing controlled permissions

    @NotNull(message = "Start date must be provided")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date must be provided")
    private LocalDateTime endDate;

    // STORY: FPMAPP-8910 - DTO for delegation request input

    public User getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(User delegateUser) {
        this.delegateUser = delegateUser;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
