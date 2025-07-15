package kr.co.amateurs.server.config.auth.http;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ProjectAuthorizeHttpRequestsConfig implements CustomAuthorizeHttpRequestsConfigurer {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/projects").hasAnyRole("ADMIN", "STUDENT")
            .requestMatchers(HttpMethod.PUT, "/api/v1/projects/**").hasAnyRole("ADMIN", "STUDENT")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**").hasAnyRole("ADMIN", "STUDENT");
    }
}
