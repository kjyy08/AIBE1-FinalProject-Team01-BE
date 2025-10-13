package kr.co.amateurs.server.domain.post.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingType {
    COFFEE_CHAT("커피챗"),
    MENTORING("멘토링");

    private final String description;
}
