package com.iwa.messaging_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.iwa.messaging_service.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Trouver les messages en fonction de l'identifiant de la conversation
    List<Message> findByConversationId(Long conversationId);
}
