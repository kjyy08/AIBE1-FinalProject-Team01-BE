package kr.co.amateurs.server.domain.file.repository;

import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.post.model.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);

    @Query("SELECT pi.imageUrl FROM PostImage pi")
    List<String> findAllUrls();
}
