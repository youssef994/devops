package com.example.notification.Service;

import com.example.notification.Model.Notification;
import com.example.notification.Repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize sample notification
        notification = new Notification();
        notification.setId(1L); // Use setId instead of setNotificationId
        notification.setUserId(1L);
        notification.setType("INFO"); // Optional: Set a type
        notification.setContent("Test notification");
        notification.setRead(false);
    }

    @Test
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification createdNotification = notificationService.createNotification(notification);

        assertNotNull(createdNotification);
        assertEquals(notification.getContent(), createdNotification.getContent());
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), eq(notification));
    }

    @Test
    void testGetNotificationsByUserId() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);

        List<Notification> foundNotifications = notificationService.getNotificationsByUserId(1L);

        assertNotNull(foundNotifications);
        assertEquals(1, foundNotifications.size());
        assertEquals(notification.getContent(), foundNotifications.get(0).getContent());
    }

    @Test
    void testMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testMarkAllAsRead() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);

        notificationService.markAllAsRead(1L);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }
}
