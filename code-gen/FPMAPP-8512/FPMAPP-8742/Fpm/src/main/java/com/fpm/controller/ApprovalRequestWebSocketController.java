package com.fpm.controller;

import com.fpm.service.ApprovalRequestWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApprovalRequestWebSocketController {

    @Autowired
    private ApprovalRequestWebSocketService websocketService;

    // STORY: FPMAPP-8742 - Endpoint to update WebSocket connection status from client
    @PostMapping("/api/websocket/status")
    public void updateWebSocketStatus(@RequestBody Map<String, Boolean> status) {
        Boolean connected = status.get("connected");
        if (connected != null) {
            websocketService.setWebSocketConnected(connected);
        }
    }

    // STORY: FPMAPP-8742 - Method to check if UI should receive real-time updates
    public boolean canSendRealTimeUpdate() {
        return websocketService.isWebSocketConnected();
    }

    // TODO: Integrate canSendRealTimeUpdate() check in existing real-time update push logic to suppress updates when disconnected
}