package kr.co.amateurs.server.domain.alarm.model.entity.metadata;

import kr.co.amateurs.server.domain.post.model.entity.enums.BoardType;

public record CommentMetaData(
        Long postId,
        BoardType boardType,
        Long commentId
) implements AlarmMetaData {
}
