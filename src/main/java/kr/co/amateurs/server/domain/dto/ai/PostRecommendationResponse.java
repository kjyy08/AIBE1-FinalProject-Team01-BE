package kr.co.amateurs.server.domain.dto.ai;

import kr.co.amateurs.server.domain.entity.post.Post;
import kr.co.amateurs.server.domain.entity.post.enums.BoardType;

import java.time.LocalDateTime;

public record PostRecommendationResponse(
        Long id,
        String title,
        String authorNickname,
        Integer likeCount,
        Integer viewCount,
        Integer commentCount,
        BoardType boardType,
        LocalDateTime createdAt
) {
    public static PostRecommendationResponse from(Post post) {
        return new PostRecommendationResponse(
                post.getId(),
                post.getTitle(),
                post.getUser().getNickname(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getComments().size(),
                post.getBoardType(),
                post.getCreatedAt()
        );
    }
}
