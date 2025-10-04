package kr.co.amateurs.server.domain.comment.model.dto;

public interface ReplyCount {
    Long getParentCommentId();
    Long getCount();
}
