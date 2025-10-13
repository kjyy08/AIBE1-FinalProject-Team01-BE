package kr.co.amateurs.server.domain.project.repository;

import kr.co.amateurs.server.domain.post.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
