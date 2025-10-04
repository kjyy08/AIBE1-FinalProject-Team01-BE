package kr.co.amateurs.server.domain.post.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPost extends BaseEntity {
    @OneToOne
    @JoinColumn(nullable = false)
    private Post post;

    public static CommunityPost from(Post post) {
        return CommunityPost.builder()
                .post(post)
                .build();
    }
}
