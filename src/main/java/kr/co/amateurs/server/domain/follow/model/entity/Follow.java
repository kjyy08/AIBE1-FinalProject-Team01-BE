package kr.co.amateurs.server.domain.follow.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import kr.co.amateurs.server.domain.user.model.entity.User;
import lombok.*;

@Entity
@Table(name = "follows")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Follow extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User fromUser;
}
