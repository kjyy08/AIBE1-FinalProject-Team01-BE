package kr.co.amateurs.server.domain.verify.model.dto;

import kr.co.amateurs.server.domain.user.model.entity.enums.Role;
import kr.co.amateurs.server.domain.verify.model.entity.VerifyStatus;

public record VerifyStatusDTO(
        String role,
        VerifyStatus status,
        String message
) {

    public static VerifyStatusDTO completed(Role role) {
        return new VerifyStatusDTO(
                role.name(),
                VerifyStatus.COMPLETED,
                "인증 완료"
        );
    }

    public static VerifyStatusDTO fromVerify(Role role, VerifyStatus status) {
        return new VerifyStatusDTO(
                role.name(),
                status,
                status.getDescription()
        );
    }

    public static VerifyStatusDTO notRequested(Role role) {
        return new VerifyStatusDTO(
                role.name(),
                null,
                "인증을 요청해주세요"
        );
    }
}