package com.iwa.messaging_service.service;

import com.iwa.messaging_service.model.Conversation;
import com.iwa.messaging_service.model.Message;
import com.iwa.messaging_service.repository.ConversationRepository;
import com.iwa.messaging_service.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessagingService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String MESSAGE_TOPIC = "messages";
    private static final String STATUS_UPDATE_TOPIC = "message-status-updates";

    /**
     * Créer une nouvelle conversation entre deux utilisateurs.
     */
    public Conversation createConversation(Long personOneId, Long personTwoId) {
        Conversation conversation = new Conversation(personOneId, personTwoId);
        return conversationRepository.save(conversation);
    }

    /**
     * Récupérer les conversations associées à un utilisateur.
     */
    public List<Conversation> getConversationsByUser(Long userId) {
        return conversationRepository.findByPersonOneIdOrPersonTwoId(userId, userId);
    }

    /**
     * Récupérer une conversation par son ID.
     */
    public Optional<Conversation> getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    /**
     * Envoyer un message dans une conversation.
     */
    public Message sendMessage(Long conversationId, Long senderId, String content) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);

        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            Message message = new Message(conversation, senderId, content);
            message.setStatus(Message.MessageStatus.SENT);

            kafkaTemplate.send(MESSAGE_TOPIC, message);
            return messageRepository.save(message);
        } else {
            throw new RuntimeException("Conversation not found");
        }
    }

    /**
     * Récupérer tous les messages d'une conversation.
     */
    public List<Message> getMessagesByConversation(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    /**
     * Mettre à jour le statut d'un message.
     */
    public Message updateMessageStatus(Long messageId, Message.MessageStatus newStatus) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);

        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setStatus(newStatus);
            messageRepository.save(message);

            // Publier la mise à jour du statut sur Kafka
            kafkaTemplate.send(STATUS_UPDATE_TOPIC, new MessageStatusUpdate(messageId, newStatus));
            return message;
        } else {
            throw new RuntimeException("Message not found");
        }
    }

    /**
     * Classe interne pour représenter les mises à jour de statut.
     */
    public static class MessageStatusUpdate {
        private Long messageId;
        private Message.MessageStatus status;

        public MessageStatusUpdate(Long messageId, Message.MessageStatus status) {
            this.messageId = messageId;
            this.status = status;
        }

        public Long getMessageId() {
            return messageId;
        }

        public Message.MessageStatus getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "MessageStatusUpdate{" +
                    "messageId=" + messageId +
                    ", status=" + status +
                    '}';
        }
    }
}