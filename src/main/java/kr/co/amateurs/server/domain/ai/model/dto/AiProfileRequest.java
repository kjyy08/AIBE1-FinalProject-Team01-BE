package kr.co.amateurs.server.domain.ai.model.dto;

import java.util.List;

public record AiProfileRequest(
        String userTopics,
        String devcourseName,
        List<PostSummaryData> activitySummaries
) {
}
