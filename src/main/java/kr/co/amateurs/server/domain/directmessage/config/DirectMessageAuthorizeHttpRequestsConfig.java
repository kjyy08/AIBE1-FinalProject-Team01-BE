package kr.co.amateurs.server.domain.ai.config;
import kr.co.amateurs.server.common.config.web.http.CustomAuthorizeHttpRequestsConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class DirectMessageAuthorizeHttpRequestsConfig implements CustomAuthorizeHttpRequestsConfigurer {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers("/api/v1/dm/**").hasAnyRole("ADMIN", "STUDENT", "GUEST");
    }
}
