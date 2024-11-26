package com.iwa.messaging_service.service;

import com.iwa.messaging_service.model.Message;
import com.iwa.messaging_service.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumerService {

    @Autowired
    private MessageRepository messageRepository;

    @KafkaListener(topics = "messaging_topic", groupId = "messaging-service-group")
    public void consumeMessage(Message message) {
        System.out.println("Message reçu: " + message.getContenu());
        messageRepository.save(message);
    }

    @KafkaListener(topics = "message_status_topic", groupId = "messaging-service-group")
    public void consumeMessageStatusUpdate(KafkaProducerService.MessageStatusUpdate statusUpdate) {
        Optional<Message> messageOpt = messageRepository.findById(statusUpdate.getMessageId());
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setStatus(statusUpdate.getStatus());
            messageRepository.save(message);
            System.out.println("Statut mis à jour pour le message ID " + message.getId() + " : " + message.getStatus());
        } else {
            System.out.println("Message non trouvé pour mise à jour du statut : ID " + statusUpdate.getMessageId());
        }
    }
}