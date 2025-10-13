package kr.co.amateurs.server.domain.verify.repository;

import kr.co.amateurs.server.domain.verify.model.entity.Verify;
import kr.co.amateurs.server.domain.verify.model.entity.VerifyStatus;
import kr.co.amateurs.server.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VerifyRepository extends JpaRepository<Verify, Long> {
    List<Verify> findByUser(User user);
    boolean existsByUserAndStatusIn(User user, List<VerifyStatus> statuses);
    Optional<Verify> findFirstByUserOrderByCreatedAtDesc(User user);
} 