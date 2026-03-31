package com.fpm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStatusUpdateDTO {
    private Long requestId;
    private String requesterUsername;
    private String currentStatus;
    private String previousStatus;
    private String updatedBy;
    private LocalDateTime updateTimestamp;
    private String message; // Optional message or comment
}