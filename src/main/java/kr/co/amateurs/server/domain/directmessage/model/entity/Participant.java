package kr.co.amateurs.server.domain.directmessage.model.entity;

import kr.co.amateurs.server.domain.post.model.entity.enums.DevCourseTrack;
import kr.co.amateurs.server.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    private Long userId;
    private String nickname;
    private String profileImage;
    private DevCourseTrack devcourseName;
    private String devcourseBatch;
    private LocalDateTime lastReadAt;
    private LocalDateTime reEntryAt;

    @Builder.Default
    private Boolean isActive = true;

    public void exitRoom() {
        this.isActive = false;
    }

    public void reEntry() {
        this.isActive = true;
        this.reEntryAt = LocalDateTime.now();
    }

    public static Participant from(User user) {
        return Participant.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getImageUrl())
                .devcourseName(user.getDevcourseName())
                .devcourseBatch(user.getDevcourseBatch())
                .build();
    }
}
