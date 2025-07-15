package kr.co.amateurs.server.repository.ai;

import kr.co.amateurs.server.domain.dto.ai.PostRecommendationResponse;
import kr.co.amateurs.server.domain.entity.ai.RecommendedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiRecommendPostRepository extends JpaRepository<RecommendedPost, Long> {
    @Query("SELECT new kr.co.amateurs.server.domain.dto.ai.PostRecommendationResponse(" +
            "p.id, p.title, u.nickname, p.likeCount, ps.viewCount, " +
            "CAST((SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id) AS integer), " +
            "p.boardType, p.createdAt, rp.boardId) " +
            "FROM RecommendedPost rp " +
            "JOIN rp.post p " +
            "JOIN p.user u " +
            "JOIN PostStatistics ps ON ps.postId = p.id " +
            "WHERE rp.user.id = :userId " +
            "ORDER BY rp.id")
    List<PostRecommendationResponse> findRecommendationDataByUserId(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}
