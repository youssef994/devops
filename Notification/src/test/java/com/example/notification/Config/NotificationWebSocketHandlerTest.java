package com.example.notification.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationWebSocketHandlerTest {

    private NotificationWebSocketHandler handler;

    @Mock
    private WebSocketSession mockSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new NotificationWebSocketHandler();
    }

    @Test
    public void testAfterConnectionEstablishedAddsSession() throws Exception {
        // Set up mock HttpHeaders with a userId
        HttpHeaders headers = new HttpHeaders();
        headers.add("userId", "123");
        when(mockSession.getHandshakeHeaders()).thenReturn(headers);

        handler.afterConnectionEstablished(mockSession);

        // Verify the session is added to the handler's session map
        Map<Long, WebSocketSession> sessions = handler.getSessions();
        assertTrue(sessions.containsKey(123L), "Session should be added for userId 123");
        assertEquals(mockSession, sessions.get(123L), "Session should match the mock session");
    }

    @Test
    public void testAfterConnectionClosedRemovesSession() throws Exception {
        // Set up mock HttpHeaders with a userId and add it to the sessions map
        HttpHeaders headers = new HttpHeaders();
        headers.add("userId", "123");
        when(mockSession.getHandshakeHeaders()).thenReturn(headers);

        handler.afterConnectionEstablished(mockSession);

        // Now close the connection and ensure the session is removed
        handler.afterConnectionClosed(mockSession, CloseStatus.NORMAL);

        Map<Long, WebSocketSession> sessions = handler.getSessions();
        assertFalse(sessions.containsKey(123L), "Session should be removed for userId 123 after connection closed");
    }
}
