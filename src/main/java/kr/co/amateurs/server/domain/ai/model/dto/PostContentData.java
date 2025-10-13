package kr.co.amateurs.server.domain.ai.model.dto;

public record PostContentData(
        Long postId,
        String title,
        String content,
        String activityType
) {
}
