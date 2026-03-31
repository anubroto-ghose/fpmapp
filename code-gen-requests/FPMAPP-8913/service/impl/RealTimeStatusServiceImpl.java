package com.fpm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fpm.dto.ApprovalStatusUpdateDTO;
import com.fpm.dto.UploadProgressUpdateDTO;
import com.fpm.service.RealTimeStatusService;

@Service
public class RealTimeStatusServiceImpl implements RealTimeStatusService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RealTimeStatusServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // STORY: FPMAPP-8913 - Push approval status update to subscribed clients
    @Override
    public void sendApprovalStatusUpdate(ApprovalStatusUpdateDTO update) {
        messagingTemplate.convertAndSend("/topic/approval/status", update);
    }

    // STORY: FPMAPP-8913 - Push upload progress update to subscribed clients
    @Override
    public void sendUploadProgressUpdate(UploadProgressUpdateDTO update) {
        messagingTemplate.convertAndSend("/topic/upload/progress", update);
    }
}