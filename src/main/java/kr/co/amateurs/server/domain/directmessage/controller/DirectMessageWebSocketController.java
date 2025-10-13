package kr.co.amateurs.server.domain.directmessage.controller;

import kr.co.amateurs.server.domain.directmessage.model.dto.DirectMessageRequest;
import kr.co.amateurs.server.domain.directmessage.model.dto.DirectMessageResponse;
import kr.co.amateurs.server.domain.directmessage.service.DirectMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DirectMessageWebSocketController {
    private final DirectMessageService directMessageService;

    @MessageMapping("/dm/room/{roomId}")
    @SendTo("/topic/dm/room/{roomId}")
    public DirectMessageResponse chat(@DestinationVariable String roomId, DirectMessageRequest directMessageRequest) {
        return directMessageService.saveMessage(roomId, directMessageRequest);
    }
}
