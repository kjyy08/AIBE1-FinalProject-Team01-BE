package kr.co.amateurs.server.domain.entity.auth;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String email;

    private String token;

    @TimeToLive
    private Long expiresIn;

    @Builder
    public RefreshToken(String email, String token, Long expiresIn) {
        this.email = email;
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
