package kr.co.amateurs.server.service.auth;

import kr.co.amateurs.server.domain.common.ErrorCode;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.domain.entity.user.enums.ProviderType;
import kr.co.amateurs.server.domain.entity.user.enums.Role;
import kr.co.amateurs.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 시도: provider={}", provider);

        log.info("GitHub에서 받아온 모든 속성: {}", oAuth2User.getAttributes());

        String providerId = getProviderId(oAuth2User, provider);
        String email = getEmail(oAuth2User, provider);
        String nickname = getNickname(oAuth2User, provider);
        String name = getName(oAuth2User, provider);
        String imageUrl = getImageUrl(oAuth2User, provider);

        log.info("파싱된 정보 - email: '{}', nickname: '{}', name: '{}'", email, nickname, name);

        try {
            saveOrUpdateUser(provider, providerId, email, nickname, name, imageUrl);
        } catch (Exception e) {
            log.error("OAuth 사용자 등록 실패: {}", e.getMessage());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("user_registration_error"),
                    ErrorCode.OAUTH_USER_REGISTRATION_FAILED.getMessage());
        }

        return oAuth2User;
    }

    private void saveOrUpdateUser(String provider, String providerId, String email, String nickname, String name, String imageUrl) {

        Optional<User> existingUser = userRepository.findByProviderIdAndProviderType (providerId, ProviderType.valueOf(provider.toUpperCase()));

        if (existingUser.isPresent()) {
            log.info("기존 사용자 로그인: userId={}", existingUser.get().getId());
            return;
        }

        if (email != null && !email.isEmpty()) {
            if (userRepository.existsByEmail(email)) {
                log.warn("이미 다른 계정으로 가입된 이메일: {}", email);
                throw ErrorCode.OAUTH_EMAIL_ALREADY_EXISTS.get();
            }
        }

        String uniqueNickname = generateUniqueNickname(nickname);
        String finalEmail = email != null ? email : generateFakeEmail(providerId);

        User newUser = User.builder()
                .providerId(providerId)
                .providerType(ProviderType.valueOf(provider.toUpperCase()))
                .email(finalEmail)
                .nickname(uniqueNickname)
                .name(name)
                .imageUrl(imageUrl)
                .role(Role.GUEST)
                .build();

        userRepository.save(newUser);
        log.info("신규 사용자 등록: userId={}, 닉네임={}, 이메일={}", newUser.getId(), uniqueNickname, finalEmail);
    }


    private String generateUniqueNickname (String originalNickname) {
        if (originalNickname == null || originalNickname.isEmpty()) {
            originalNickname = "사용자";
        }

        if (originalNickname.length() > 10) {
            originalNickname = originalNickname.substring(0, 10);
        }

        String uniqueNickname;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            int randomNum = (int) (Math.random() * 9000) + 1000;
            uniqueNickname = originalNickname + randomNum;
            attempts++;

            if (attempts > maxAttempts) {
                uniqueNickname = originalNickname + System.currentTimeMillis();
                break;
            }
        } while (userRepository.existsByNickname(uniqueNickname));

        return uniqueNickname;
    }

    private String generateFakeEmail(String providerId) {
        return "github_" + providerId + "@amateurs.local";
    }

    private String getProviderId(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = validateAndGetAttributes(oAuth2User, provider);

        Object id = attributes.get("id");
        if (id == null) {
            log.error("GitHub에서 사용자 ID를 받지 못했습니다: {}", attributes);
            throw ErrorCode.OAUTH_USER_REGISTRATION_FAILED.get();
        }
        return String.valueOf(id);
    }

    private String getEmail(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = validateAndGetAttributes(oAuth2User, provider);
        return (String) attributes.get("email");
    }

    private String getNickname(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = validateAndGetAttributes(oAuth2User, provider);

        String nickname = (String) attributes.get("login");
        if (nickname == null) {
            log.error("GitHub에서 닉네임을 받지 못했습니다: {}", attributes);
            throw ErrorCode.OAUTH_USER_REGISTRATION_FAILED.get();
        }
        return nickname;
    }

    private String getName(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = validateAndGetAttributes(oAuth2User, provider);

        String name = (String) attributes.get("name");
        return name != null ? name : (String) attributes.get("login");
    }

    private String getImageUrl(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = validateAndGetAttributes(oAuth2User, provider);
        return (String) attributes.get("avatar_url");
    }

    private Map<String, Object> validateAndGetAttributes(OAuth2User oAuth2User, String provider) {
        if (!"github".equalsIgnoreCase(provider)) {
            log.error("지원하지 않는 OAuth Provider: {}", provider);
            throw ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED.get();
        }

        return oAuth2User.getAttributes();
    }
}
