package com.iwa.messaging_service.controller;

import com.iwa.messaging_service.model.Conversation;
import com.iwa.messaging_service.model.Message;
import com.iwa.messaging_service.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messaging")
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    /**
     * Créer une nouvelle conversation entre deux utilisateurs.
     */
    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(
            @RequestParam Long personOneId,
            @RequestParam Long personTwoId) {
        Conversation conversation = messagingService.createConversation(personOneId, personTwoId);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Récupérer les conversations associées à un utilisateur.
     */
    @GetMapping("/users/{userId}/conversations")
    public ResponseEntity<List<Conversation>> getConversations(@PathVariable Long userId) {
        List<Conversation> conversations = messagingService.getConversationsByUser(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Envoyer un message dans une conversation.
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long conversationId,
            @RequestParam Long senderId,
            @RequestParam String content) {
        Message message = messagingService.sendMessage(conversationId, senderId, content);
        return ResponseEntity.ok(message);
    }

    /**
     * Récupérer tous les messages d'une conversation.
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<Message>> getMessagesByConversation(@PathVariable Long conversationId) {
        List<Message> messages = messagingService.getMessagesByConversation(conversationId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Mettre à jour le statut d'un message.
     */
    @PatchMapping("/messages/{messageId}/status")
    public ResponseEntity<Message> updateMessageStatus(
            @PathVariable Long messageId,
            @RequestParam Message.MessageStatus newStatus) {
        Message updatedMessage = messagingService.updateMessageStatus(messageId, newStatus);
        return ResponseEntity.ok(updatedMessage);
    }
}