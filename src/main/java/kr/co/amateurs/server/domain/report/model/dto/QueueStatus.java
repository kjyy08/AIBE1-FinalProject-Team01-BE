package kr.co.amateurs.server.domain.report.model.dto;

public record QueueStatus(
        int queueSize,
        boolean isRunning,
        boolean isThreadAlive
) {}
