package kr.co.amateurs.server.domain.auth.repository;

import kr.co.amateurs.server.domain.auth.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
