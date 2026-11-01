package tech.codehunt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import tech.codehunt.dao.ChatMessageDao;
import tech.codehunt.model.ChatMessage;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageDao chatMessageDao;

    // Live Message Receive & Save in Database
    @MessageMapping("/chat/{bookingId}")
    public void sendMessage(@DestinationVariable Long bookingId, @Payload ChatMessage chatMessage) {
        chatMessage.setBookingId(bookingId);
        ChatMessage savedMessage = chatMessageDao.save(chatMessage);

        messagingTemplate.convertAndSend("/topic/chat/" + bookingId, savedMessage);
    }

    // Nayi API: Purani Chat History kisi bhi device me load karne ke liye
    @GetMapping("/api/chat/history/{bookingId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long bookingId) {
        return ResponseEntity.ok(chatMessageDao.findByBookingIdOrderByTimestampAsc(bookingId));
    }
}