package com.iwa.messaging_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
public class Message {

    public enum MessageStatus {
        SENT, RECEIVED, OPENED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Conversation conversation;

    private Long senderId;

    private String contenu;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    protected Message() {
    }

    public Message(Conversation conversation, Long senderId, String contenu) {
        this.conversation = conversation;
        this.senderId = senderId;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
        this.status = MessageStatus.SENT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getContenu() {
        return contenu;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}