package kr.co.amateurs.server.repository.directmessage;

import kr.co.amateurs.server.domain.entity.directmessage.DirectMessage;
import kr.co.amateurs.server.domain.entity.directmessage.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DirectMessageRepository extends MongoRepository<DirectMessage, String> {
    void deleteAllByRoomId(String roomId);

    Page<DirectMessage> findByRoomIdOrderBySentAtDesc(String roomId, Pageable pageable);

    Page<DirectMessage> findByRoomIdAndSentAtAfterOrderBySentAtDesc(String roomId, LocalDateTime after, Pageable pageable);

    List<DirectMessage> findByRoomIdAndMessageTypeIn(String roomId, List<MessageType> messageTypes);
}
