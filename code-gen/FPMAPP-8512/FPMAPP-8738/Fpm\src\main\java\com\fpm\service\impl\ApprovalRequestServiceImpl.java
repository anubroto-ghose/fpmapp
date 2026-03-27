package com.fpm.service.impl;

import com.fpm.service.ApprovalRequestService;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    private final AtomicBoolean websocketConnected = new AtomicBoolean(true); // Assume initially connected

    // STORY: FPMAPP-8738 - Mark WebSocket session as disconnected
    @Override
    public void markSessionDisconnected(String sessionId) {
        // TODO: Extend to handle multiple sessions if needed
        websocketConnected.set(false);
    }

    // STORY: FPMAPP-8738 - Check if WebSocket session is connected
    @Override
    public boolean isSessionConnected() {
        return websocketConnected.get();
    }

    // Other existing service methods...
}