package kr.co.amateurs.server.domain.report.service.handler;

import kr.co.amateurs.server.common.model.dto.ErrorCode;
import kr.co.amateurs.server.domain.comment.model.entity.Comment;
import kr.co.amateurs.server.domain.report.model.entity.Report;
import kr.co.amateurs.server.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentTargetHandler extends ReportTargetHandler {

    private final CommentRepository commentRepository;

    @Override
    public boolean isAlreadyBlinded(Report report) {
        return report.getComment() != null && report.getComment().getIsBlinded();
    }

    @Override
    public Long getTargetId(Report report) {
        return report.getComment().getId();
    }

    @Override
    public void blindTarget(Report report) {
        Comment comment = commentRepository.findById(report.getComment().getId())
                .orElseThrow(ErrorCode.NOT_FOUND);
        comment.updateBlinded(true);
        commentRepository.save(comment);
    }

    @Override
    public String getTargetType() {
        return "COMMENT";
    }
}