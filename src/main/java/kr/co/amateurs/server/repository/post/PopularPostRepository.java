package kr.co.amateurs.server.repository.post;

import kr.co.amateurs.server.domain.dto.post.PopularPostRequest;
import kr.co.amateurs.server.domain.entity.post.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.generated.Tables.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PopularPostRepository {
    private final DSLContext dsl;

    public List<PopularPostRequest> findRecentPostsWithCounts(LocalDateTime threeDaysAgo) {
        return dsl.select(
                        POSTS.ID,
                        POSTS.VIEW_COUNT,
                        POSTS.LIKE_COUNT,
                        DSL.count(COMMENTS.ID).as("commentCount"),
                        USERS.NICKNAME,
                        USERS.DEVCOURSE_NAME,
                        POSTS.CREATED_AT,
                        POSTS.TITLE,
                        POSTS.BOARD_TYPE
                )
                .from(POSTS)
                .join(USERS).on(USERS.ID.eq(POSTS.USER_ID))  // User JOIN 추가
                .leftJoin(COMMENTS).on(COMMENTS.POST_ID.eq(POSTS.ID))
                .where(POSTS.IS_DELETED.eq(false))
                .and(POSTS.CREATED_AT.ge(threeDaysAgo))
                .groupBy(POSTS.ID, POSTS.VIEW_COUNT, POSTS.LIKE_COUNT,
                        USERS.NICKNAME, USERS.DEVCOURSE_NAME, POSTS.CREATED_AT,
                        POSTS.TITLE, POSTS.BOARD_TYPE)
                .fetch()
                .map(record -> new PopularPostRequest(
                        record.get(POSTS.ID),
                        record.get(POSTS.VIEW_COUNT),
                        record.get(POSTS.LIKE_COUNT),
                        record.get("commentCount", Integer.class),
                        null,
                        null,
                        record.get(USERS.NICKNAME),
                        record.get(USERS.DEVCOURSE_NAME),
                        record.get(POSTS.CREATED_AT),
                        record.get(POSTS.TITLE),
                        record.get(POSTS.BOARD_TYPE, String.class)
                ));
    }

    public void savePopularPosts(List<PopularPostRequest> requests) {
        if (requests.isEmpty()) return;

        var batch = dsl.batch(
                dsl.insertInto(POPULAR_POSTS)
                        .columns(
                                POPULAR_POSTS.POST_ID,
                                POPULAR_POSTS.POPULARITY_SCORE,
                                POPULAR_POSTS.CALCULATED_DATE,
                                POPULAR_POSTS.VIEW_COUNT,
                                POPULAR_POSTS.LIKE_COUNT,
                                POPULAR_POSTS.COMMENT_COUNT,
                                POPULAR_POSTS.AUTHOR_NICKNAME,
                                POPULAR_POSTS.AUTHOR_DEVCOURSE_NAME,
                                POPULAR_POSTS.POST_CREATED_AT,
                                POPULAR_POSTS.TITLE,
                                POPULAR_POSTS.BOARD_TYPE
                        )
                        .values((Long) null, null, null, null, null, null,
                                null, null, null, null, null)
        );

        for (PopularPostRequest request : requests) {
            batch.bind(
                    request.postId(),
                    request.popularityScore(),
                    request.calculatedDate(),
                    request.viewCount(),
                    request.likeCount(),
                    request.commentCount(),
                    request.authorNickname(),
                    request.authorDevcourseName(),
                    request.postCreatedAt(),
                    request.title(),
                    request.boardType()
            );
        }

        batch.execute();
        log.info("인기글 {}개 저장 완료", requests.size());
    }

    public void deleteBeforeDate(LocalDate date) {
        int count = dsl.deleteFrom(POPULAR_POSTS)
                .where(POPULAR_POSTS.CALCULATED_DATE.lt(date))
                .execute();
        log.info("날짜 {} 인기글 {}개 삭제", date, count);
    }

    public List<PopularPostRequest> findLatestPopularPosts(int limit) {
        var latestDate = dsl.select(DSL.max(POPULAR_POSTS.CALCULATED_DATE))
                .from(POPULAR_POSTS)
                .fetchOneInto(LocalDate.class);

        if (latestDate == null) return List.of();

        return dsl.select(
                        POPULAR_POSTS.POST_ID,
                        POPULAR_POSTS.VIEW_COUNT,
                        POPULAR_POSTS.LIKE_COUNT,
                        POPULAR_POSTS.COMMENT_COUNT,
                        POPULAR_POSTS.POPULARITY_SCORE,
                        POPULAR_POSTS.CALCULATED_DATE,
                        POPULAR_POSTS.AUTHOR_NICKNAME,
                        POPULAR_POSTS.AUTHOR_DEVCOURSE_NAME,
                        POPULAR_POSTS.POST_CREATED_AT,
                        POPULAR_POSTS.TITLE,
                        POPULAR_POSTS.BOARD_TYPE
                )
                .from(POPULAR_POSTS)
                .where(POPULAR_POSTS.CALCULATED_DATE.eq(latestDate))
                .orderBy(POPULAR_POSTS.POPULARITY_SCORE.desc())
                .limit(limit)
                .fetch()
                .map(record -> new PopularPostRequest(
                        record.get(POPULAR_POSTS.POST_ID),
                        record.get(POPULAR_POSTS.VIEW_COUNT),
                        record.get(POPULAR_POSTS.LIKE_COUNT),
                        record.get(POPULAR_POSTS.COMMENT_COUNT),
                        record.get(POPULAR_POSTS.POPULARITY_SCORE),
                        record.get(POPULAR_POSTS.CALCULATED_DATE),
                        record.get(POPULAR_POSTS.AUTHOR_NICKNAME),
                        record.get(POPULAR_POSTS.AUTHOR_DEVCOURSE_NAME),
                        record.get(POPULAR_POSTS.POST_CREATED_AT),
                        record.get(POPULAR_POSTS.TITLE),
                        record.get(POPULAR_POSTS.BOARD_TYPE)
                ));
    }

    public boolean existsByDate(LocalDate date) {
        return dsl.fetchExists(
                dsl.selectFrom(POPULAR_POSTS)
                        .where(POPULAR_POSTS.CALCULATED_DATE.eq(date))
        );
    }
}
