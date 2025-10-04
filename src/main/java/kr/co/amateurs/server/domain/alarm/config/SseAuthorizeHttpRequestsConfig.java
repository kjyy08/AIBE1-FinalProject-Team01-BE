package kr.co.amateurs.server.domain.alarm.config;
import kr.co.amateurs.server.common.config.web.http.CustomAuthorizeHttpRequestsConfigurer;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class SseAuthorizeHttpRequestsConfig implements CustomAuthorizeHttpRequestsConfigurer {

    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers(HttpMethod.GET, "/api/v1/sse/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/sse/**").permitAll();
    }
}
