package com.iwa.messaging_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.iwa.messaging_service.model.Conversation;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByPersonOneIdOrPersonTwoId(Long personOneId, Long personTwoId);

}
