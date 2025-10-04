package kr.co.amateurs.server.domain.directmessage.model.dto;

import kr.co.amateurs.server.domain.directmessage.model.entity.DirectMessage;
import kr.co.amateurs.server.domain.directmessage.model.entity.enums.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DirectMessageResponse(
        String id,
        String roomId,
        String content,
        Long senderId,
        String senderNickname,
        String senderProfileImage,
        MessageType messageType,
        LocalDateTime sentAt
) {
    public static DirectMessageResponse fromCollection(DirectMessage message) {
        return new DirectMessageResponse(
                message.getId(),
                message.getRoomId(),
                message.getContent(),
                message.getSenderId(),
                message.getSenderNickname(),
                message.getSenderProfileImage(),
                message.getMessageType(),
                message.getSentAt()
        );
    }
}
