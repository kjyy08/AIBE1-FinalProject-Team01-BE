package kr.co.amateurs.server.domain.ai.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.domain.ai.model.dto.AiProfileResponse;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import kr.co.amateurs.server.domain.user.model.entity.User;
import lombok.*;

@Entity
@Table(name = "ai_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Lob
    @Column(name = "persona_description", nullable = false, columnDefinition = "TEXT")
    private String personaDescription;

    @Column(name = "interest_keywords", nullable = false, length = 1024)
    private String interestKeywords;

    public void updateProfile(AiProfileResponse response) {
        this.personaDescription = response.personaDescription();
        this.interestKeywords = response.interestKeywords();
    }
}
