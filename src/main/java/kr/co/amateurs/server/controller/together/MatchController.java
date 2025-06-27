package kr.co.amateurs.server.controller.together;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.amateurs.server.domain.dto.common.PageResponseDTO;
import kr.co.amateurs.server.annotation.boardaccess.BoardAccess;
import kr.co.amateurs.server.domain.dto.together.MatchPostRequestDTO;
import kr.co.amateurs.server.domain.dto.together.MatchPostResponseDTO;
import kr.co.amateurs.server.domain.dto.common.PostPaginationParam;
import kr.co.amateurs.server.domain.entity.post.enums.BoardType;
import kr.co.amateurs.server.domain.entity.post.enums.Operation;
import kr.co.amateurs.server.service.together.MatchService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
@Tag(name="Gathering Post", description = "함께해요 게시판의 커피챗/멘토링 탭 API")
public class MatchController {
    
    private final MatchService matchService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @BoardAccess(hasBoardType = false, boardType = BoardType.MATCH)
    @GetMapping
    public ResponseEntity<PageResponseDTO<MatchPostResponseDTO>> getMatchPostList(
            @ParameterObject @Valid PostPaginationParam paginationParam
            ){
        PageResponseDTO<MatchPostResponseDTO> matchList = matchService.getMatchPostList(paginationParam);
        return ResponseEntity.ok(matchList);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @BoardAccess(hasPostId = true)
    @GetMapping("/{postId}")
    public ResponseEntity<MatchPostResponseDTO> getMatchPost(
            @PathVariable("postId") Long postId){
        MatchPostResponseDTO gatherPost = matchService.getMatchPost(postId);
        return ResponseEntity.ok(gatherPost);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @BoardAccess(hasBoardType = false, boardType = BoardType.MATCH, operation = Operation.WRITE)
    @PostMapping
    public ResponseEntity<MatchPostResponseDTO> createMatchPost(
            @RequestBody @Valid MatchPostRequestDTO dto){
        MatchPostResponseDTO post = matchService.createMatchPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @BoardAccess(hasPostId = true, checkAuthor = true, operation = Operation.WRITE)
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updateMatchPost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid MatchPostRequestDTO dto){
        matchService.updateMatchPost(postId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //TODO - Soft Delete 로 변경 시 PATCH 요청으로 변경 예정
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @BoardAccess(hasPostId = true, checkAuthor = true, operation = Operation.WRITE)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteMatchPost(
            @PathVariable("postId") Long postId){
        matchService.deleteMatchPost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
