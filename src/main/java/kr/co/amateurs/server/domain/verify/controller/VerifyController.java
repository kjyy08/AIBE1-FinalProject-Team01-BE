package kr.co.amateurs.server.domain.verify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.amateurs.server.common.config.jwt.CustomUserDetails;
import kr.co.amateurs.server.domain.verify.model.dto.VerifyResultDTO;
import kr.co.amateurs.server.domain.verify.model.dto.VerifyStatusDTO;
import kr.co.amateurs.server.domain.post.model.entity.enums.DevCourseTrack;
import kr.co.amateurs.server.domain.user.model.entity.User;
import kr.co.amateurs.server.domain.verify.service.VerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
@Tag(name = "Verify", description = "수강생 인증 API")
public class VerifyController {
    private final VerifyService verifyService;

    @PostMapping(value = "/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "수강생 인증 요청", description = "프로그래머스 데브코스 수강생 인증을 요청합니다. 비동기로 처리됩니다.")
    public ResponseEntity<VerifyResultDTO> requestVerification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam("devcourseName") DevCourseTrack devcourseName,
            @RequestParam("devcourseBatch") String devcourseBatch,
            @RequestParam("image") MultipartFile image) {

        User user = currentUser.getUser();
        VerifyResultDTO result = verifyService.verifyStudent(user, image, devcourseName, devcourseBatch);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    @Operation(summary = "인증 상태 조회", description = "현재 사용자의 인증 상태를 조회합니다.")
    public ResponseEntity<VerifyStatusDTO> getVerificationStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        User user = currentUser.getUser();
        VerifyStatusDTO status = verifyService.getVerificationStatus(user);
        return ResponseEntity.ok(status);
    }
}
