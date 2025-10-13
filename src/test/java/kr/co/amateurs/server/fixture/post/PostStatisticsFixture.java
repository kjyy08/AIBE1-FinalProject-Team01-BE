package kr.co.amateurs.server.fixture.post;

import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.post.model.entity.PostStatistics;

public class PostStatisticsFixture {
    public static PostStatistics createDefault(Post post){
        return PostStatistics.builder()
                .post(post)
                .viewCount(0)
                .build();
    }
}
