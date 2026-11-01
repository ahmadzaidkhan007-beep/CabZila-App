package tech.codehunt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.codehunt.model.ChatMessage;
import java.util.List;

@Repository
public interface ChatMessageDao extends JpaRepository<ChatMessage, Long> {
    // Ye method apne aap ek ride ki saari purani chat history time ke hisaab se le aayega
    List<ChatMessage> findByBookingIdOrderByTimestampAsc(Long bookingId);
}