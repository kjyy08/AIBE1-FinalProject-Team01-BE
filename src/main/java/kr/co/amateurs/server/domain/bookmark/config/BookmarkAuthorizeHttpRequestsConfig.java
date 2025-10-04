package kr.co.amateurs.server.domain.bookmark.config;
import kr.co.amateurs.server.common.config.http.CustomAuthorizeHttpRequestsConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class BookmarkAuthorizeHttpRequestsConfig implements CustomAuthorizeHttpRequestsConfigurer {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers("/api/v1/bookmarks/**").hasAnyRole("ADMIN", "STUDENT", "GUEST");
    }
}
