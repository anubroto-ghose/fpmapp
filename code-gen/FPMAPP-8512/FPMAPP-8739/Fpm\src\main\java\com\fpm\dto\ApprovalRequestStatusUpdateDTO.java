package com.fpm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestStatusUpdateDTO {
    private Long requestId;
    private String status;
    private String message;
    private boolean realTimeConnectionActive; // STORY: FPMAPP-8739 - indicate websocket connection status
}
