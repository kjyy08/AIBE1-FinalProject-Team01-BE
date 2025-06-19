package kr.co.amateurs.server.repository.together;

import kr.co.amateurs.server.domain.entity.post.GatheringPost;
import kr.co.amateurs.server.domain.entity.post.MatchingPost;
import kr.co.amateurs.server.domain.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<MatchingPost, Long> {
    @Query("""
      select mp
        from MatchingPost mp
        join mp.post p
       where p.title   like concat('%', :keyword, '%')
          or p.content like concat('%', :keyword, '%')
    """)
        // TODO - 쿼리에 아래 코드 추가 시 검색어가 태그에도 포함되는 지 확인 가능
        // or p.tags    like concat('%', :keyword, '%')
    Page<MatchingPost> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
