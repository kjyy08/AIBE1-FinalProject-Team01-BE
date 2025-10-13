package kr.co.amateurs.server.domain.report.service.handler;

import kr.co.amateurs.server.common.model.dto.ErrorCode;
import kr.co.amateurs.server.domain.report.model.entity.enums.ReportTarget;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReportTargetHandlerFactory {
    private final Map<ReportTarget, ReportTargetHandler> handlers;

    public ReportTargetHandlerFactory(PostTargetHandler postHandler,
                                      CommentTargetHandler commentHandler) {
        this.handlers = Map.of(
                ReportTarget.POST, postHandler,
                ReportTarget.COMMENT, commentHandler
        );
    }

    public ReportTargetHandler getHandler(ReportTarget target) {
        ReportTargetHandler handler = handlers.get(target);
        if (handler == null) {
            throw ErrorCode.NOT_FOUND.get();
        }
        return handler;
    }
}
