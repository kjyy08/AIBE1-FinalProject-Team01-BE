package kr.co.amateurs.server.domain.comment.model.dto;

import java.util.List;

public record CommentPageDTO(
        List<CommentResponseDTO> comments,
        Long nextCursor,
        boolean hasNext
) {
}
