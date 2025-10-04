package kr.co.amateurs.server.domain.alarm.config;
import kr.co.amateurs.server.common.config.http.CustomAuthorizeHttpRequestsConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AlarmAuthorizeHttpRequestsConfig implements CustomAuthorizeHttpRequestsConfigurer {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers("/api/v1/alarms/**").hasAnyRole("ADMIN", "STUDENT", "GUEST");
    }
}
