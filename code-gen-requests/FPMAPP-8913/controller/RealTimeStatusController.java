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

    // STORY: FPMAPP-8913 - Endpoint to receive approval status updates and broadcast to subscribers
    @MessageMapping("/approval/status/update")
    @SendTo("/topic/approval/status")
    public ApprovalStatusUpdateDTO sendApprovalStatusUpdate(ApprovalStatusUpdateDTO update) {
        // This method can be used if clients send updates, but typically backend pushes updates
        return update;
    }

    // STORY: FPMAPP-8913 - Endpoint to receive upload progress updates and broadcast to subscribers
    @MessageMapping("/upload/progress/update")
    @SendTo("/topic/upload/progress")
    public UploadProgressUpdateDTO sendUploadProgressUpdate(UploadProgressUpdateDTO update) {
        return update;
    }

    // STORY: FPMAPP-8913 - Method for backend services to push approval status updates to clients
    public void pushApprovalStatusUpdate(ApprovalStatusUpdateDTO update) {
        messagingTemplate.convertAndSend("/topic/approval/status", update);
    }

    // STORY: FPMAPP-8913 - Method for backend services to push upload progress updates to clients
    public void pushUploadProgressUpdate(UploadProgressUpdateDTO update) {
        messagingTemplate.convertAndSend("/topic/upload/progress", update);
    }
}