package kr.co.amateurs.server.domain.verify.model.event;

import kr.co.amateurs.server.domain.post.model.entity.enums.DevCourseTrack;
import kr.co.amateurs.server.domain.user.model.entity.User;

public record VerificationEvent(
        Long verifyId,
        String imageUrl,
        String filename,
        User user,
        DevCourseTrack devcourseName,
        String devcourseBatch
) {
}
