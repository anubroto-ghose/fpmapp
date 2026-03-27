package com.fpm.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

@Service
public class ApprovalRequestWebSocketService {

    private final AtomicBoolean websocketConnected = new AtomicBoolean(true);

    // STORY: FPMAPP-8742 - Track WebSocket connection status to prevent UI updates when disconnected
    public boolean isWebSocketConnected() {
        return websocketConnected.get();
    }

    // STORY: FPMAPP-8742 - Update WebSocket connection status
    public void setWebSocketConnected(boolean connected) {
        websocketConnected.set(connected);
    }
}