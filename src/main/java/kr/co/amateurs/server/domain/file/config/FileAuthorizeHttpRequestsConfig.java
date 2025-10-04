package kr.co.amateurs.server.domain.file.config;
import kr.co.amateurs.server.common.config.http.CustomAuthorizeHttpRequestsConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class FileAuthorizeHttpRequestsConfig implements CustomAuthorizeHttpRequestsConfigurer {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers("/api/v1/upload/**").hasAnyRole("GUEST", "STUDENT", "ADMIN");
    }
}
