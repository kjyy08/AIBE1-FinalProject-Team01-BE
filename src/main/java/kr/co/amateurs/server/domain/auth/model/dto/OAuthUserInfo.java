package kr.co.amateurs.server.domain.auth.model.dto;

import kr.co.amateurs.server.domain.user.model.entity.enums.ProviderType;
import lombok.Builder;

@Builder
public record OAuthUserInfo(
        ProviderType providerType,
        String providerId,
        String email,
        String nickname,
        String name,
        String imageUrl
) {
}