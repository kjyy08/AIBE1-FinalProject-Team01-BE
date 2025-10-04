package kr.co.amateurs.server.domain.post.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "it_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ITPost extends BaseEntity {
    @OneToOne
    @JoinColumn(nullable = false)
    private Post post;

    public static ITPost from(Post post) {
        return ITPost.builder()
                .post(post)
                .build();
    }
}
