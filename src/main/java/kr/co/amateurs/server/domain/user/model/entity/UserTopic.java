package kr.co.amateurs.server.domain.user.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import kr.co.amateurs.server.domain.user.model.entity.enums.Topic;
import lombok.*;

@Entity
@Table(name = "user_topics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserTopic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Topic topic;
}
