package kr.co.amateurs.server.domain.alarm.model.entity.metadata;

public record DirectMessageMetaData(
        String roomId,
        String messageId
) implements AlarmMetaData {
}
