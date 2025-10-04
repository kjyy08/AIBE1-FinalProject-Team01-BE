package kr.co.amateurs.server.domain.user.repository;

import kr.co.amateurs.server.domain.user.model.entity.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTopicRepository extends JpaRepository<UserTopic, Long> {
}
