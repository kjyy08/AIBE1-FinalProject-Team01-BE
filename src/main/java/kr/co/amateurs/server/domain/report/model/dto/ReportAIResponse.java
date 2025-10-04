package kr.co.amateurs.server.domain.report.model.dto;

public record ReportAIResponse(
    boolean isViolation,
    String reason,
    Double confidenceScore
){}
