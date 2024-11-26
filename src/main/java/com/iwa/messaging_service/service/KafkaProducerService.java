package com.iwa.messaging_service.service;

import com.iwa.messaging_service.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String MESSAGE_TOPIC = "messaging_topic";
    private static final String STATUS_TOPIC = "message_status_topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Message message) {
        kafkaTemplate.send(MESSAGE_TOPIC, message);
        System.out.println("Message envoyé: " + message.getContenu());
    }

    public void sendMessageStatusUpdate(Long messageId, Message.MessageStatus status) {
        kafkaTemplate.send(STATUS_TOPIC, new MessageStatusUpdate(messageId, status));
        System.out.println("Statut mis à jour pour le message ID " + messageId + " : " + status);
    }

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
    }
}