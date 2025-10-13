package kr.co.amateurs.server.domain.ai.model.dto;

import kr.co.amateurs.server.domain.post.model.entity.enums.BoardType;

import java.time.LocalDateTime;

public record PostRecommendationResponse(
        Long id,
        String title,
        String authorNickname,
        Integer likeCount,
        Integer viewCount,
        Integer commentCount,
        BoardType boardType,
        LocalDateTime createdAt,
        Long boardId
) {
}
