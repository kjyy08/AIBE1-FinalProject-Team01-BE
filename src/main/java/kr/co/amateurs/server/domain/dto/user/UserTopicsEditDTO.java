package kr.co.amateurs.server.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.domain.entity.user.enums.Topic;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record UserTopicsEditDTO(
        @Schema(description = "관심 주제 목록", example = "[\"FRONTEND\", \"BACKEND\"]")
        Set<Topic> topics
) {
        public static UserTopicsEditDTO from(User user) {
                return UserTopicsEditDTO.builder()
                        .topics(user.getUserTopics().stream()
                                .map(userTopic -> userTopic.getTopic())
                                .collect(Collectors.toSet()))
                        .build();
        }
}