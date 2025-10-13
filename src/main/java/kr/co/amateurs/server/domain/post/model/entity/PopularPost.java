package kr.co.amateurs.server.domain.post.model.entity;


import jakarta.persistence.*;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import kr.co.amateurs.server.domain.post.model.entity.enums.BoardType;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "popular_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PopularPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private Double popularityScore;

    @Column(nullable = false)
    private LocalDate calculatedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @Column(nullable = false)
    private Long boardId;
}