package kr.co.amateurs.server.fixture.project;

import kr.co.amateurs.server.domain.comment.model.entity.Comment;
import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.user.model.entity.User;

public class CommentFixture {
    public static Comment createComment(Post post, User user, String content) {
        return Comment.builder()
                .user(user)
                .postId(post.getId())
                .content(content)
                .build();
    }

    public static Comment createReplyComment(Post post, User user, Comment parentComment, String content) {
        return Comment.builder()
                .user(user)
                .postId(post.getId())
                .content(content)
                .parentCommentId(parentComment.getId())
                .build();
    }
}
