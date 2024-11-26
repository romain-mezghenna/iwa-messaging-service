package com.iwa.messaging_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwa.messaging_service.model.Conversation;
import com.iwa.messaging_service.model.Message;
import com.iwa.messaging_service.model.Message.MessageStatus;
import com.iwa.messaging_service.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessagingController.class)
@Import(ObjectMapper.class) // Import ObjectMapper for JSON serialization
public class MessagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessagingService messagingService;

    @Autowired
    private WebApplicationContext context;

    private Conversation mockConversation;
    private Message mockMessage;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Create mock Conversation
        mockConversation = new Conversation(1L, 2L);
        mockConversation.setId(1L);

        // Create mock Message
        mockMessage = new Message(mockConversation, 1L, "Hello World");
        mockMessage.setId(1L);
        mockMessage.setDate(LocalDateTime.now());
        mockMessage.setStatus(MessageStatus.SENT);
    }

    @Test
    @WithMockUser
    public void testCreateConversation() throws Exception {
        Mockito.when(messagingService.createConversation(anyLong(), anyLong())).thenReturn(mockConversation);

        mockMvc.perform(post("/messaging/conversations")
                        .param("personOneId", "1")
                        .param("personTwoId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.personOneId").value(1))
                .andExpect(jsonPath("$.personTwoId").value(2));
    }

    @Test
    @WithMockUser
    public void testGetConversations() throws Exception {
        List<Conversation> conversations = Arrays.asList(mockConversation);
        Mockito.when(messagingService.getConversationsByUser(1L)).thenReturn(conversations);

        mockMvc.perform(get("/messaging/users/1/conversations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].personOneId").value(1))
                .andExpect(jsonPath("$[0].personTwoId").value(2));
    }

    @Test
    @WithMockUser
    public void testSendMessage() throws Exception {
        Mockito.when(messagingService.sendMessage(anyLong(), anyLong(), anyString())).thenReturn(mockMessage);

        mockMvc.perform(post("/messaging/messages")
                        .param("conversationId", "1")
                        .param("senderId", "1")
                        .param("content", "Hello World")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contenu").value("Hello World"))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    @WithMockUser
    public void testGetMessagesByConversation() throws Exception {
        List<Message> messages = Arrays.asList(mockMessage);
        Mockito.when(messagingService.getMessagesByConversation(1L)).thenReturn(messages);

        mockMvc.perform(get("/messaging/conversations/1/messages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contenu").value("Hello World"))
                .andExpect(jsonPath("$[0].senderId").value(1));
    }

    @Test
    @WithMockUser
    public void testUpdateMessageStatus() throws Exception {
        mockMessage.setStatus(MessageStatus.RECEIVED);
        Mockito.when(messagingService.updateMessageStatus(anyLong(), any(MessageStatus.class))).thenReturn(mockMessage);

        mockMvc.perform(patch("/messaging/messages/1/status")
                        .param("newStatus", "RECEIVED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }
}