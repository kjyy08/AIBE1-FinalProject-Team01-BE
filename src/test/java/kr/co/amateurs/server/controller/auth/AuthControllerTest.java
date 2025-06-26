package kr.co.amateurs.server.controller.auth;

import kr.co.amateurs.server.fixture.auth.AuthTestFixture;
import kr.co.amateurs.server.fixture.auth.TokenTestFixture;
import kr.co.amateurs.server.fixture.common.UserTestFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import kr.co.amateurs.server.config.EmbeddedRedisConfig;
import kr.co.amateurs.server.domain.dto.auth.LoginRequestDto;
import kr.co.amateurs.server.domain.dto.auth.SignupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Import(EmbeddedRedisConfig.class)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 정상적인_로그인_요청_시_JWT_토큰이_반환되어야_한다() {
        // given
        SignupRequestDto signupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        LoginRequestDto loginRequest = AuthTestFixture.defaultLoginRequest()
                .email(signupRequest.email())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("tokenType", equalTo(TokenTestFixture.TOKEN_TYPE))
                .body("expiresIn", equalTo(TokenTestFixture.ACCESS_TOKEN_EXPIRATION.intValue()));
    }

    @Test
    void 존재하지_않는_사용자로_로그인_시_404_에러가_발생해야_한다() {
        // given
        LoginRequestDto request = AuthTestFixture.createNonExistentUserLoginRequest();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("존재하지 않는 사용자입니다."));
    }

    @Test
    void 잘못된_비밀번호로_로그인_시_400_에러가_발생해야_한다() {
        // given
        SignupRequestDto signupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        LoginRequestDto wrongPasswordRequest = AuthTestFixture.defaultLoginRequest()
                .email(signupRequest.email())
                .password(TokenTestFixture.getWrongPassword())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(wrongPasswordRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("비밀번호가 일치하지 않습니다."));
    }

    @Test
    void 잘못된_이메일_형식으로_로그인_시_400_에러가_발생해야_한다() {
        // given
        LoginRequestDto invalidEmailRequest = AuthTestFixture.createInvalidEmailLoginRequest();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(invalidEmailRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("올바른 이메일 형식이 아닙니다"));
    }

    @Test
    void 빈_이메일로_로그인_시_400_에러가_발생해야_한다() {
        // given
        LoginRequestDto emptyEmailRequest = AuthTestFixture.createEmptyEmailLoginRequest();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(emptyEmailRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("이메일은 필수입니다"));
    }

    @Test
    void 빈_비밀번호로_로그인_시_400_에러가_발생해야_한다() {
        // given
        LoginRequestDto emptyPasswordRequest = AuthTestFixture.createEmptyPasswordLoginRequest();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(emptyPasswordRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("비밀번호는 필수입니다"));
    }

    @Test
    void 정상적인_회원가입_요청_시_성공해야_한다() {
        // given
        SignupRequestDto signupRequest = UserTestFixture.createUniqueSignupRequest();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201)
                .body("userId", notNullValue())
                .body("email", equalTo(signupRequest.email()))
                .body("nickname", equalTo(signupRequest.nickname()))
                .body("name", equalTo(signupRequest.name()));
    }

    @Test
    void 이메일_중복_시_400_에러가_발생해야_한다() {
        // given
        SignupRequestDto firstSignupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(firstSignupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        SignupRequestDto duplicateEmailRequest = UserTestFixture.defaultSignupRequest()
                .email(firstSignupRequest.email())
                .nickname(UserTestFixture.generateUniqueNickname())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(duplicateEmailRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(400)
                .body("message", equalTo("이미 사용 중인 이메일입니다."));
    }

    @Test
    void 닉네임_중복_시_400_에러가_발생해야_한다() {
        // given
        SignupRequestDto firstSignupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(firstSignupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        SignupRequestDto duplicateNicknameRequest = UserTestFixture.defaultSignupRequest()
                .email(UserTestFixture.generateUniqueEmail())
                .nickname(firstSignupRequest.nickname())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(duplicateNicknameRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(400)
                .body("message", equalTo("이미 사용 중인 닉네임입니다."));
    }

    @Test
    void 잘못된_이메일_형식으로_회원가입_시_400_에러가_발생해야_한다() {
        // given
        SignupRequestDto invalidEmailRequest = UserTestFixture.defaultSignupRequest()
                .email("invalid-email")
                .nickname(UserTestFixture.generateUniqueNickname())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(invalidEmailRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(400)
                .body("message", equalTo("올바른 이메일 형식이 아닙니다"));
    }

    @Test
    void 빈_이메일로_회원가입_시_400_에러가_발생해야_한다() {
        // given
        SignupRequestDto emptyEmailRequest = UserTestFixture.defaultSignupRequest()
                .email("")
                .nickname(UserTestFixture.generateUniqueNickname())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(emptyEmailRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(400)
                .body("message", equalTo("이메일은 필수입니다"));
    }

    @Test
    void 짧은_비밀번호로_회원가입_시_400_에러가_발생해야_한다() {
        // given
        SignupRequestDto shortPasswordRequest = UserTestFixture.defaultSignupRequest()
                .email(UserTestFixture.generateUniqueEmail())
                .nickname(UserTestFixture.generateUniqueNickname())
                .password("1234567")
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(shortPasswordRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(400)
                .body("message", equalTo("비밀번호는 최소 8자 이상이어야 합니다"));
    }

    @Test
    void 정상적인_로그인_시_리프레시_토큰도_함께_반환되어야_한다() {
        // given
        SignupRequestDto signupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        LoginRequestDto loginRequest = AuthTestFixture.defaultLoginRequest()
                .email(signupRequest.email())
                .build();

        // when & then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("tokenType", equalTo(TokenTestFixture.TOKEN_TYPE))
                .body("expiresIn", equalTo(TokenTestFixture.ACCESS_TOKEN_EXPIRATION.intValue()));
    }

    @Test
    void 사용_가능한_이메일_확인_시_true를_반환한다() {
        // given
        String availableEmail = UserTestFixture.generateUniqueEmail();

        // when & then
        RestAssured.given()
                .param("email", availableEmail)
                .when()
                .get("/api/v1/auth/check/email")
                .then()
                .statusCode(200)
                .body("available", equalTo(true))
                .body("message", equalTo("사용 가능한 이메일입니다"));
    }

    @Test
    void 이미_사용중인_이메일_확인_시_false를_반환한다() {
        // given
        SignupRequestDto signupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        // when & then
        RestAssured.given()
                .param("email", signupRequest.email())
                .when()
                .get("/api/v1/auth/check/email")
                .then()
                .statusCode(200)
                .body("available", equalTo(false))
                .body("message", equalTo("이미 사용중인 이메일입니다"));
    }

    @Test
    void 사용_가능한_닉네임_확인_시_true를_반환한다() {
        // given
        String availableNickname = UserTestFixture.generateUniqueNickname();

        // when & then
        RestAssured.given()
                .param("nickname", availableNickname)
                .when()
                .get("/api/v1/auth/check/nickname")
                .then()
                .statusCode(200)
                .body("available", equalTo(true))
                .body("message", equalTo("사용 가능한 닉네임입니다"));
    }

    @Test
    void 이미_사용중인_닉네임_확인_시_false를_반환한다() {
        // given
        SignupRequestDto signupRequest = UserTestFixture.createUniqueSignupRequest();
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/v1/auth/signup")
                .then()
                .statusCode(201);

        // when & then
        RestAssured.given()
                .param("nickname", signupRequest.nickname())
                .when()
                .get("/api/v1/auth/check/nickname")
                .then()
                .statusCode(200)
                .body("available", equalTo(false))
                .body("message", equalTo("이미 사용중인 닉네임입니다"));

    }

    @Test
    void 빈_이메일로_중복_확인_시_400_에러가_발생해야_한다() {
        // given
        String emptyEmail = "";

        // when & then
        RestAssured.given()
                .param("email", emptyEmail)
                .when()
                .get("/api/v1/auth/check/email")
                .then()
                .statusCode(400)
                .body("message", equalTo("이메일은 필수입니다."));
    }

    @Test
    void 잘못된_이메일_형식으로_중복_확인_시_400_에러가_발생해야_한다() {
        // given
        String invalidEmail = "invalid-email";

        // when & then
        RestAssured.given()
                .param("email", invalidEmail)
                .when()
                .get("/api/v1/auth/check/email")
                .then()
                .statusCode(400)
                .body("message", equalTo("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    void 빈_닉네임으로_중복_확인_시_400_에러가_발생해야_한다() {
        // given
        String emptyNickname = "";

        // when & then
        RestAssured.given()
                .param("nickname", emptyNickname)
                .when()
                .get("/api/v1/auth/check/nickname")
                .then()
                .statusCode(400)
                .body("message", equalTo("닉네임은 필수입니다."));
    }

    @Test
    void 긴_닉네임으로_중복_확인_시_400_에러가_발생해야_한다() {
        // given
        String longNickname = "a".repeat(21);

        // when & then
        RestAssured.given()
                .param("nickname", longNickname)
                .when()
                .get("/api/v1/auth/check/nickname")
                .then()
                .statusCode(400)
                .body("message", equalTo("닉네임은 2자 이상 20자 이하여야 합니다."));
    }
}
