package kr.co.amateurs.server.domain.directmessage.model.dto.event;

import kr.co.amateurs.server.domain.user.model.entity.User;

public record AnonymizeEvent(
        User user
) {
}
