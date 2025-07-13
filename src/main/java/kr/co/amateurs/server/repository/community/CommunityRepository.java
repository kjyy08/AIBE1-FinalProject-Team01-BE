package kr.co.amateurs.server.repository.community;

import kr.co.amateurs.server.domain.dto.community.CommunityResponseDTO;
import kr.co.amateurs.server.domain.entity.post.CommunityPost;
import kr.co.amateurs.server.domain.entity.post.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {
    @Query("""
        SELECT new kr.co.amateurs.server.domain.dto.community.CommunityResponseDTO(
            cp.id,
            p.id,
            p.title,
            p.content,
            u.nickname,
            u.imageUrl,
            u.devcourseName,
            u.devcourseBatch,
            p.boardType,
            p.isBlinded,
            ps.viewCount,
            p.likeCount,
            CAST(COUNT(c) AS int),
            CAST(COUNT(b) AS int),
            p.createdAt,
            p.updatedAt,
            p.tags,
            false,
            false
        )
        FROM CommunityPost cp
        JOIN cp.post p
        JOIN p.user u
        JOIN PostStatistics ps ON ps.postId = p.id
        LEFT JOIN Comment c ON c.postId = p.id AND c.isDeleted = false
        LEFT JOIN Bookmark b ON b.post.id = p.id AND b.user.id = u.id
        WHERE p.boardType = :boardType
        GROUP BY cp.id, p.id
        """)
    Page<CommunityResponseDTO> findDTOByBoardType(@Param("boardType") BoardType boardType, Pageable pageable);

    @Query("""
            SELECT new kr.co.amateurs.server.domain.dto.community.CommunityResponseDTO(
                cp.id,
                p.id,
                p.title,
                p.content,
                u.nickname,
                u.imageUrl,
                u.devcourseName,
                u.devcourseBatch,
                p.boardType,
                p.isBlinded,
                ps.viewCount,
                p.likeCount,
                CAST(COUNT(c) AS int),
                CAST(COUNT(b) AS int),
                p.createdAt,
                p.updatedAt,
                p.tags,
                false,
                false
            )
            FROM CommunityPost cp
            JOIN cp.post p
            JOIN p.user u
            JOIN PostStatistics ps ON ps.postId = p.id
            LEFT JOIN Comment c ON c.postId = p.id AND c.isDeleted = false
            LEFT JOIN Bookmark b ON b.post.id = p.id AND b.user.id = u.id
            WHERE p.boardType = :boardType
              AND (:keyword IS NULL
                   OR :keyword = ''
                   OR p.title LIKE CONCAT('%', :keyword, '%')
                   OR p.content LIKE CONCAT('%', :keyword, '%'))
            GROUP BY cp.id, p.id
            """)
    Page<CommunityResponseDTO> findDTOByContentAndBoardType(@Param("keyword") String keyword,
                                                            @Param("boardType") BoardType boardType,
                                                            Pageable pageable);

    @Query("""
        SELECT new kr.co.amateurs.server.domain.dto.community.CommunityResponseDTO(
            cp.id, p.id, p.title, p.content, u.nickname, u.imageUrl, 
            u.devcourseName, u.devcourseBatch, p.boardType,p.isBlinded, ps.viewCount, 
            p.likeCount, CAST(COUNT(DISTINCT c.id) AS int), CAST(COUNT(DISTINCT b.id) AS int), p.createdAt, 
            p.updatedAt, p.tags,
            CASE WHEN pl.id IS NOT NULL THEN true ELSE false END,
            CASE WHEN b.id IS NOT NULL THEN true ELSE false END
        )
        FROM CommunityPost cp
        JOIN cp.post p JOIN p.user u
        JOIN PostStatistics ps ON ps.postId = p.id
        LEFT JOIN Comment c ON c.postId = p.id AND c.isDeleted = false
        LEFT JOIN Like pl ON pl.post.id = p.id AND pl.user.id = :userId
        LEFT JOIN Bookmark b ON b.post.id = p.id AND b.user.id = :userId
        WHERE cp.id = :communityId
        GROUP BY cp.id, p.id
        """)
    Optional<CommunityResponseDTO> findDTOByIdForUser(@Param("communityId") Long communityId,
                                                      @Param("userId") Long userId);

    @Query("""
            SELECT new kr.co.amateurs.server.domain.dto.community.CommunityResponseDTO(
            cp.id,
            p.id,
            p.title,
            p.content,
            u.nickname,
            u.imageUrl,
            u.devcourseName,
            u.devcourseBatch,
            p.boardType,
            p.isBlinded,
            ps.viewCount,
            p.likeCount,
            CAST(COUNT(c) AS int),
            CAST(COUNT(b) AS int),
            p.createdAt,
            p.updatedAt,
            p.tags,
            false,
            false
        )
        FROM CommunityPost cp
        JOIN cp.post p
        JOIN p.user u
        JOIN PostStatistics ps ON ps.postId = p.id
        LEFT JOIN Comment c ON c.postId = p.id AND c.isDeleted = false
        LEFT JOIN Bookmark b ON b.post.id = p.id AND b.user.id = u.id
        WHERE p.boardType = :boardType
        GROUP BY cp.id, p.id
        ORDER BY ps.viewCount DESC
        """)
    Page<CommunityResponseDTO> findDTOByBoardTypeOrderByViewCount(@Param("boardType") BoardType boardType,
                                                                  Pageable pageable);

    @Query("""
            SELECT new kr.co.amateurs.server.domain.dto.community.CommunityResponseDTO(
                cp.id,
                p.id,
                p.title,
                p.content,
                u.nickname,
                u.imageUrl,
                u.devcourseName,
                u.devcourseBatch,
                p.boardType,
                p.isBlinded,
                ps.viewCount,
                p.likeCount,
                CAST(COUNT(c) AS int),
                CAST(COUNT(b) AS int),
                p.createdAt,
                p.updatedAt,
                p.tags,
                false,
                false
            )
            FROM CommunityPost cp
            JOIN cp.post p
            JOIN p.user u
            JOIN PostStatistics ps ON ps.postId = p.id
            LEFT JOIN Comment c ON c.postId = p.id AND c.isDeleted = false
            LEFT JOIN Bookmark b ON b.post.id = p.id AND b.user.id = u.id
            WHERE p.boardType = :boardType
              AND (:keyword IS NULL
                   OR :keyword = ''
                   OR p.title LIKE CONCAT('%', :keyword, '%')
                   OR p.content LIKE CONCAT('%', :keyword, '%'))
            GROUP BY cp.id, p.id
            ORDER BY ps.viewCount DESC
            """)
    Page<CommunityResponseDTO> findDTOByContentAndBoardTypeOrderByViewCount(@Param("keyword") String keyword,
                                                                            @Param("boardType") BoardType boardType,
                                                                            Pageable pageable);
}
