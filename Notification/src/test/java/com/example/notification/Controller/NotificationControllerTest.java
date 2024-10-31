package com.example.notification.Controller;


import com.example.notification.Model.Notification;
import com.example.notification.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    public void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(123L);
        notification.setContent("Test Notification");
        notification.setRead(false);
    }

    @Test
    public void testCreateNotification() throws Exception {
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":123,\"message\":\"Test Notification\",\"read\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(123L))
                .andExpect(jsonPath("$.content").value("Test Notification"))
                .andExpect(jsonPath("$.read").value(false));

        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    public void testGetNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getNotificationsByUserId(123L)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(123L))
                .andExpect(jsonPath("$[0].content").value("Test Notification"))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService, times(1)).getNotificationsByUserId(123L);
    }

    @Test
    public void testMarkAsRead() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    public void testMarkAllAsRead() throws Exception {
        doNothing().when(notificationService).markAllAsRead(123L);

        mockMvc.perform(put("/notifications/markAllAsRead/123"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAllAsRead(123L);
    }
}
