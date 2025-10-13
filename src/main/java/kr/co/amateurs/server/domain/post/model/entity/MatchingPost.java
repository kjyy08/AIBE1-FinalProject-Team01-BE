package kr.co.amateurs.server.domain.post.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.domain.together.model.dto.MatchPostRequestDTO;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import kr.co.amateurs.server.domain.post.model.entity.enums.MatchingStatus;
import kr.co.amateurs.server.domain.post.model.entity.enums.MatchingType;
import lombok.*;

@Entity
@Table(name = "matching_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MatchingPost extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingType matchingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingStatus status;

    private String expertiseAreas;

    public void update(MatchPostRequestDTO dto){
        this.matchingType = dto.matchingType();
        this.status = dto.status();
        this.expertiseAreas = dto.expertiseArea();
    }

    public void updateStatus(MatchingStatus status) {
        this.status = status;
    }
}
