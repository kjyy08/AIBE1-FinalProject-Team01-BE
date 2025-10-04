package kr.co.amateurs.server.domain.user.repository;

import kr.co.amateurs.server.domain.user.model.entity.User;
import kr.co.amateurs.server.domain.user.model.entity.enums.Role;
import kr.co.amateurs.server.domain.user.model.entity.enums.ProviderType;
import kr.co.amateurs.server.domain.user.model.entity.enums.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderIdAndProviderType(String providerId, ProviderType providerType);

    User findByNickname(String nickname);

    @Query("SELECT ut.topic FROM UserTopic ut WHERE ut.user.id = :userId")
    List<Topic> findTopicDisplayNamesByUserId(@Param("userId") Long userId);

    Page<User> findByRoleIn(List<Role> roles, Pageable pageable);
}
