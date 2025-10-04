package kr.co.amateurs.server.fixture.project;

import kr.co.amateurs.server.domain.bookmark.model.entity.Bookmark;
import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.user.model.entity.User;

public class BookmarkFixture {
    public static Bookmark createBookmark(User user, Post post) {
        return Bookmark.builder()
                .user(user)
                .post(post)
                .build();
    }
}
