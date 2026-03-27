package com.fpm.service;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8738 - Mark WebSocket session as disconnected
    void markSessionDisconnected(String sessionId);

    // STORY: FPMAPP-8738 - Check if WebSocket session is connected
    boolean isSessionConnected();

    // Other existing service methods...
}