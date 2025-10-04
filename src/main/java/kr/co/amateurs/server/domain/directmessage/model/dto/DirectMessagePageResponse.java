package kr.co.amateurs.server.domain.directmessage.model.dto;

import kr.co.amateurs.server.common.model.dto.PageInfo;
import kr.co.amateurs.server.domain.directmessage.model.entity.DirectMessage;
import org.springframework.data.domain.Page;

import java.util.List;

public record DirectMessagePageResponse(
        List<DirectMessageResponse> messages,
        PageInfo pageInfo
) {
    public static DirectMessagePageResponse from(Page<DirectMessage> page) {
        List<DirectMessageResponse> messageResponses = page.getContent().stream()
                .map(DirectMessageResponse::fromCollection)
                .toList();
        PageInfo info = PageInfo.from(page);

        return new DirectMessagePageResponse(messageResponses, info);
    }
}
