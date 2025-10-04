package kr.co.amateurs.server.domain.ai.controller;

import kr.co.amateurs.server.common.controller.AbstractControllerTest;
import io.restassured.RestAssured;
import kr.co.amateurs.server.domain.ai.model.dto.PostRecommendationResponse;
import kr.co.amateurs.server.domain.post.model.dto.PopularPostResponse;
import kr.co.amateurs.server.domain.post.model.entity.enums.BoardType;
import kr.co.amateurs.server.domain.user.model.entity.User;
import kr.co.amateurs.server.fixture.common.UserTestFixture;
import kr.co.amateurs.server.domain.user.repository.UserRepository;
import kr.co.amateurs.server.domain.ai.service.PostRecommendService;
import kr.co.amateurs.server.domain.post.service.PopularPostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AiControllerTest extends AbstractControllerTest {

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    PostRecommendService postRecommendService;

    @MockitoBean
    PopularPostService popularPostService;

    User user;
    String accessToken;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Nested
    class AI_추천_및_인기글_API {
        @Test
        void AI_개인화_게시글_조회_정상() {
            // given (인증 필요)
            user = UserTestFixture.createUser();
            user = userRepository.save(user);
            accessToken = jwtProvider.generateAccessToken(user.getEmail());

            List<PostRecommendationResponse> mockList = List.of(
                new PostRecommendationResponse(1L, "추천1", "닉네임", 10, 100, 5, BoardType.FREE, LocalDateTime.now(), 1L)
            );
            when(postRecommendService.getStoredRecommendations(anyLong(), anyInt())).thenReturn(mockList);

            // when & then
            RestAssured
                .given().header("Authorization", "Bearer " + accessToken)
                .when().get("/ai/posts/recommendations?limit=1")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", PostRecommendationResponse.class);
        }

        @Test
        void 인기글_조회_정상() {
            // given (인증 불필요)
            List<PopularPostResponse> mockList = List.of(
                new PopularPostResponse(1L, "인기글", "닉네임", 10, 100, 5, BoardType.FREE, LocalDateTime.now(), 1L)
            );
            when(popularPostService.getPopularPosts(anyInt())).thenReturn(mockList);

            // when & then
            RestAssured
                .when().get("/ai/posts/popular?limit=1")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", PopularPostResponse.class);
        }
        
        @Test
        void 추천_게시글이_없으면_빈_리스트를_반환한다() {
            // given (인증 필요)
            user = UserTestFixture.createUser();
            user = userRepository.save(user);
            accessToken = jwtProvider.generateAccessToken(user.getEmail());
            when(postRecommendService.getStoredRecommendations(anyLong(), anyInt())).thenReturn(List.of());

            // when & then
            List<PostRecommendationResponse> result = RestAssured
                .given().header("Authorization", "Bearer " + accessToken)
                .when().get("/ai/posts/recommendations?limit=1")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", PostRecommendationResponse.class);
            org.assertj.core.api.Assertions.assertThat(result).isEmpty();
        }

    
    }
} 