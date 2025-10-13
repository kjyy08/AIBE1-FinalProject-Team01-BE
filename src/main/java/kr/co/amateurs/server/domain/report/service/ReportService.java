package kr.co.amateurs.server.domain.report.service;

import kr.co.amateurs.server.common.model.dto.ErrorCode;
import kr.co.amateurs.server.domain.report.model.dto.ReportCreatedEvent;
import kr.co.amateurs.server.domain.report.model.dto.ReportRequestDTO;
import kr.co.amateurs.server.domain.report.model.dto.ReportResponseDTO;
import kr.co.amateurs.server.domain.comment.model.entity.Comment;
import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.report.model.entity.Report;
import kr.co.amateurs.server.domain.report.model.entity.enums.ReportStatus;
import kr.co.amateurs.server.domain.report.model.entity.enums.ReportTarget;
import kr.co.amateurs.server.domain.user.model.entity.User;
import kr.co.amateurs.server.domain.comment.repository.CommentRepository;
import kr.co.amateurs.server.domain.post.repository.PostRepository;
import kr.co.amateurs.server.domain.report.repository.ReportRepository;
import kr.co.amateurs.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ApplicationEventPublisher eventPublisher;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UserService userService;

    public Page<ReportResponseDTO> getReports(ReportTarget reportTarget, ReportStatus status , int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String targetName = (reportTarget != null) ? reportTarget.name() : null;
        Page<Report> reports = reportRepository.findByStatusAndType(status, targetName, pageable);

        return reports.map(ReportResponseDTO::from);
    }

    @Transactional
    public ReportResponseDTO createReport(ReportRequestDTO requestDTO) {
        User user = userService.getCurrentLoginUser();

        Report report = createReportEntity(requestDTO, user);
        if (isDuplicateReport(requestDTO, user)) {
            throw ErrorCode.DUPLICATE_REPORT.get();
        }

        Report savedReport = reportRepository.save(report);

        eventPublisher.publishEvent(new ReportCreatedEvent(savedReport.getId()));

        return ReportResponseDTO.from(savedReport);
    }

    @Transactional
    public void updateStatusReport(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId).orElseThrow(ErrorCode.REPORT_NOT_FOUND);

        report.updateStatusReport(status);
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(ErrorCode.REPORT_NOT_FOUND);

        reportRepository.delete(report);
    }

    private Report createReportEntity(ReportRequestDTO requestDTO, User user) {
        return switch (requestDTO.reportTarget()) {
            case POST -> {
                Post post = postRepository.findById(requestDTO.targetId())
                        .orElseThrow(ErrorCode.NOT_FOUND);
                yield Report.fromPost(post, user, requestDTO);
            }
            case COMMENT -> {
                Comment comment = commentRepository.findById(requestDTO.targetId())
                        .orElseThrow(ErrorCode.NOT_FOUND);
                yield Report.fromComment(comment, user, requestDTO);
            }
        };
    }

    private boolean isDuplicateReport(ReportRequestDTO requestDTO, User user) {
        return switch (requestDTO.reportTarget()) {
            case POST -> reportRepository.existsByUserIdAndPostIdAndReportType(
                    user.getId(), requestDTO.targetId(), requestDTO.reportType());
            case COMMENT -> reportRepository.existsByUserIdAndCommentIdAndReportType(
                    user.getId(), requestDTO.targetId(), requestDTO.reportType());
        };
    }
}
