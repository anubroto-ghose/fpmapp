package com.fpm.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.fpm.dto.ApprovalStatusUpdateDTO;
import com.fpm.dto.UploadProgressUpdateDTO;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class RealTimeStatusController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RealTimeStatusController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // STORY: FPMAPP-8913 - Endpoint to send approval status updates to subscribed clients
    public void sendApprovalStatusUpdate(ApprovalStatusUpdateDTO update) {
        // TODO: Add any filtering or role-based logic if needed before sending
        messagingTemplate.convertAndSend("/topic/approval-status/" + update.getUserId(), update);
    }

    // STORY: FPMAPP-8913 - Endpoint to send upload progress updates to subscribed clients
    public void sendUploadProgressUpdate(UploadProgressUpdateDTO update) {
        // TODO: Add any filtering or role-based logic if needed before sending
        messagingTemplate.convertAndSend("/topic/upload-progress/" + update.getUserId(), update);
    }

}
