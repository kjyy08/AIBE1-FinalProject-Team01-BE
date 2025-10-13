package kr.co.amateurs.server.domain.directmessage.model.dto;

import kr.co.amateurs.server.domain.directmessage.model.entity.DirectMessageRoom;
import kr.co.amateurs.server.domain.directmessage.model.entity.Participant;
import kr.co.amateurs.server.domain.post.model.entity.enums.DevCourseTrack;
import kr.co.amateurs.server.domain.user.model.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DirectMessageRoomResponse(
        String id,
        Long partnerId,
        String partnerNickname,
        String partnerProfileImage,
        DevCourseTrack devcourseName,
        String devcourseBatch,
        String lastMessage,
        LocalDateTime sentAt
) {
    public static DirectMessageRoomResponse fromCollection(DirectMessageRoom room, User currentUser) {
        List<Participant> participants = room.getParticipants();
        Participant partner = participants.get(0).getUserId().equals(currentUser.getId())
                ? participants.get(1)
                : participants.get(0);

        return new DirectMessageRoomResponse(
                room.getId(),
                partner.getUserId(),
                partner.getNickname(),
                partner.getProfileImage(),
                partner.getDevcourseName(),
                partner.getDevcourseBatch(),
                room.getLastMessage(),
                room.getSentAt()
        );
    }
}
