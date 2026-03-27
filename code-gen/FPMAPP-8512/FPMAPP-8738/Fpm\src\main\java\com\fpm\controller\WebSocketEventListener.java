package com.fpm.controller;

import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8738 - Listen for WebSocket disconnect events and mark session disconnected
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        approvalRequestService.markSessionDisconnected(sessionId);
        // TODO: Optionally notify UI or log the disconnect event
    }
}