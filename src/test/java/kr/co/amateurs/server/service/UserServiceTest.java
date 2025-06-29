package kr.co.amateurs.server.service;

import kr.co.amateurs.server.config.EmbeddedRedisConfig;
import kr.co.amateurs.server.config.TestAuthHelper;
import kr.co.amateurs.server.config.jwt.CustomUserDetails;
import kr.co.amateurs.server.domain.dto.user.*;
import kr.co.amateurs.server.domain.entity.topic.UserTopic;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.domain.entity.user.enums.Role;
import kr.co.amateurs.server.domain.entity.user.enums.Topic;
import kr.co.amateurs.server.exception.CustomException;
import kr.co.amateurs.server.fixture.common.UserTestFixture;
import kr.co.amateurs.server.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(EmbeddedRedisConfig.class)
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = UserTestFixture.defaultUser()
                .email(UserTestFixture.generateUniqueEmail())
                .nickname(UserTestFixture.generateUniqueNickname())
                .password(passwordEncoder.encode(UserTestFixture.DEFAULT_PASSWORD))
                .role(Role.GUEST)
                .imageUrl("https://example.com/profile.jpg")
                .build();
        testUser.addUserTopics(Set.of(Topic.FRONTEND, Topic.BACKEND));

        testUser = TestAuthHelper.setAuthentication(testUser, userRepository);
    }

    @Test
    void 사용자_엔티티를_프로필_응답_DTO로_올바르게_변환한다() {
        // given
        User savedUser = userRepository.save(testUser);

        // when
        UserProfileResponseDto result = UserProfileResponseDto.from(savedUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(savedUser.getId());
        assertThat(result.email()).isEqualTo(savedUser.getEmail());
        assertThat(result.nickname()).isEqualTo(savedUser.getNickname());
        assertThat(result.name()).isEqualTo(savedUser.getName());
        assertThat(result.topics()).containsExactlyInAnyOrder(Topic.FRONTEND, Topic.BACKEND);
    }

    @Test
    void 토픽_Lazy_Loading이_정상_작동한다() {
        // given
        User savedUser = userRepository.save(testUser);

        // when
        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        // then
        assertThatNoException().isThrownBy(() -> {
            Set<Topic> topics = foundUser.getUserTopics().stream()
                    .map(UserTopic::getTopic)
                    .collect(Collectors.toSet());

            assertThat(topics).hasSize(2);
            assertThat(topics).containsExactlyInAnyOrder(Topic.FRONTEND, Topic.BACKEND);
        });
    }

    @Test
    void 기본_정보_수정_요청_시_정상적으로_업데이트된다() {
        // given
        UserBasicProfileEditRequestDto request = UserBasicProfileEditRequestDto.builder()
                .name("변경된이름")
                .nickname("changedNick")
                .imageUrl("https://example.com/new-profile.jpg")
                .build();

        // when
        UserBasicProfileEditResponseDto response = userService.updateBasicProfile(request);

        // then
        assertThat(response.name()).isEqualTo("변경된이름");
        assertThat(response.nickname()).isEqualTo("changedNick");
        assertThat(response.imageUrl()).isEqualTo("https://example.com/new-profile.jpg");

        User updatedUser = userRepository.findByEmail(testUser.getEmail()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("변경된이름");
        assertThat(updatedUser.getNickname()).isEqualTo("changedNick");
        assertThat(updatedUser.getImageUrl()).isEqualTo("https://example.com/new-profile.jpg");
    }

    @Test
    void 올바른_비밀번호로_변경_시_정상적으로_업데이트된다() {
        // given
        UserPasswordEditRequestDto request = UserPasswordEditRequestDto.builder()
                .currentPassword(UserTestFixture.DEFAULT_PASSWORD)
                .newPassword("newPassword123")
                .build();

        // when
        UserPasswordEditResponseDto response = userService.updatePassword(request);

        // then
        assertThat(response.message()).isEqualTo("비밀번호가 성공적으로 변경되었습니다");

        User updatedUser = userRepository.findByEmail(testUser.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword123", updatedUser.getPassword())).isTrue();
    }

    @Test
    void 잘못된_현재_비밀번호_입력_시_예외가_발생한다() {
        // given
        UserPasswordEditRequestDto request = UserPasswordEditRequestDto.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(request))
                .isInstanceOf(CustomException.class);
    }
}