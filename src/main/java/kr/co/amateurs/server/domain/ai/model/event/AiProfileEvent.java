package kr.co.amateurs.server.domain.ai.model.event;

public record AiProfileEvent(
        Long userId,
        String context
) {
}