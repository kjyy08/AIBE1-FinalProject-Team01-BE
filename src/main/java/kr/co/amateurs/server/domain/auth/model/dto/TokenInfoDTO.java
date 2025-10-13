package kr.co.amateurs.server.domain.auth.model.dto;

import lombok.Builder;

@Builder
public record TokenInfoDTO(
        String accessToken,
        Long accessTokenExpiresIn,
        String refreshToken,
        Long refreshTokenExpiresIn
) {
    public static TokenInfoDTO of (String accessToken, Long accessTokenExpiresIn,
                                   String refreshToken, Long refreshTokenExpiresIn) {
        return TokenInfoDTO.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .build();
    }
}
