package kr.co.amateurs.server.domain.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.amateurs.server.domain.entity.like.Like;

public record LikeResponseDTO(
        @Schema(description = "좋아요 대상 종류", example = "Post")
        String targetType,
        @Schema(description = "대상 ID", example = "1")
        Long id
) {
    public static LikeResponseDTO convertToDTO(Like like, String targetType) {
        if(targetType.equals("comment")) {
            return new LikeResponseDTO(targetType, like.getComment().getId());
        }
        return new LikeResponseDTO(targetType, like.getPost().getId());
    }
}
