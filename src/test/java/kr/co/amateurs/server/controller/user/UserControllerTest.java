package kr.co.amateurs.server.controller.user;

import io.restassured.http.ContentType;
import kr.co.amateurs.server.config.EmbeddedRedisConfig;
import kr.co.amateurs.server.controller.common.AbstractControllerTest;
import kr.co.amateurs.server.domain.dto.user.UserBasicProfileEditRequestDto;
import kr.co.amateurs.server.domain.dto.user.UserPasswordEditRequestDto;
import kr.co.amateurs.server.domain.dto.user.UserTopicsEditRequestDto;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.domain.entity.user.enums.Role;
import kr.co.amateurs.server.domain.entity.user.enums.Topic;
import kr.co.amateurs.server.fixture.auth.TokenTestFixture;
import kr.co.amateurs.server.fixture.common.UserTestFixture;
import kr.co.amateurs.server.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Import(EmbeddedRedisConfig.class)
public class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String accessToken;
    private String rawPassword = "password123";

    @BeforeEach
    void setUp() {
        testUser = UserTestFixture.defaultUser()
                .email(UserTestFixture.generateUniqueEmail())
                .nickname(UserTestFixture.generateUniqueNickname())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.GUEST)
                .imageUrl("https://example.com/profile.jpg")
                .build();
        testUser.addUserTopics(Set.of(Topic.FRONTEND, Topic.BACKEND));
        testUser = userRepository.save(testUser);

        accessToken = jwtProvider.generateAccessToken(testUser.getEmail());
    }

    @Test
    void 로그인된_사용자의_프로필_조회가_성공한다() {
        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(testUser.getEmail()))
                .body("nickname", equalTo(testUser.getNickname()))
                .body("name", equalTo(testUser.getName()))
                .body("topics", not(empty()));
    }

    @Test
    void 인증_토큰_없이_프로필_조회_시_404_에러가_발생한다() {
        // when & then
        given()
                .when()
                .get("/users/me")
                .then()
                .statusCode(404);
    }

    @Test
    void 잘못된_토큰으로_프로필_조회_시_404_에러가_발생한다() {
        // given
        String invalidToken = TokenTestFixture.INVALID_TOKEN;

        // when & then
        given()
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .get("/users/me")
                .then()
                .statusCode(404);
    }

    @Test
    void 인증된_사용자가_기본_정보_수정_요청_시_정상적으로_업데이트된다() {
        // given
        String uniqueNickname = UserTestFixture.generateUniqueNickname();
        UserBasicProfileEditRequestDto updateRequest = UserBasicProfileEditRequestDto.builder()
                .name("변경된이름")
                .nickname(uniqueNickname)
                .imageUrl("https://example.com/new-profile.jpg")
                .build();

        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/users/profile/basic")
                .then()
                .statusCode(200)
                .body("name", equalTo("변경된이름"))
                .body("nickname", equalTo(uniqueNickname))
                .body("imageUrl", equalTo("https://example.com/new-profile.jpg"));
    }

    @Test
    void 인증_토큰_없이_기본_정보_수정_요청_시_403_에러가_발생한다() {
        // given
        UserBasicProfileEditRequestDto updateRequest = UserBasicProfileEditRequestDto.builder()
                .name("변경된이름")
                .build();

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/users/profile/basic")
                .then()
                .statusCode(403);
    }

    @Test
    void 인증된_사용자가_올바른_비밀번호로_변경_요청_시_정상적으로_업데이트된다() {
        // given
        UserPasswordEditRequestDto passwordRequest = UserPasswordEditRequestDto.builder()
                .currentPassword(rawPassword)
                .newPassword("newPassword123")
                .build();

        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(passwordRequest)
                .when()
                .put("/users/profile/password")
                .then()
                .statusCode(200)
                .body("message", equalTo("비밀번호가 성공적으로 변경되었습니다"));
    }

    @Test
    void 인증된_사용자가_잘못된_현재_비밀번호로_변경_요청_시_400_에러가_발생한다() {
        // given
        UserPasswordEditRequestDto passwordRequest = UserPasswordEditRequestDto.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .build();

        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(passwordRequest)
                .when()
                .put("/users/profile/password")
                .then()
                .statusCode(400);
    }

    @Test
    void 인증된_사용자가_유효한_토픽으로_변경_요청_시_정상적으로_업데이트된다() {
        // given
        UserTopicsEditRequestDto topicsRequest = UserTopicsEditRequestDto.builder()
                .topics(Set.of(Topic.BACKEND, Topic.DATA, Topic.MOBILE))
                .build();

        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(topicsRequest)
                .when()
                .put("/users/profile/topics")
                .then()
                .statusCode(200)
                .body("topics", hasSize(3))
                .body("topics", hasItems("BACKEND", "DATA", "MOBILE"));
    }

    @Test
    void 인증된_사용자가_빈_토픽으로_변경_요청_시_400_에러가_발생한다() {
        // given
        UserTopicsEditRequestDto topicsRequest = UserTopicsEditRequestDto.builder()
                .topics(Set.of())
                .build();

        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(topicsRequest)
                .when()
                .put("/users/profile/topics")
                .then()
                .statusCode(400);
    }

    @Test
    void 인증된_사용자가_4개_이상_토픽으로_변경_요청_시_400_에러가_발생한다() {
        // given
        UserTopicsEditRequestDto topicsRequest = UserTopicsEditRequestDto.builder()
                .topics(Set.of(Topic.FRONTEND, Topic.BACKEND, Topic.DATA, Topic.MOBILE))
                .build();

        // when & then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(topicsRequest)
                .when()
                .put("/users/profile/topics")
                .then()
                .statusCode(400);
    }
}