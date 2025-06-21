package kr.co.amateurs.server.controller.together;

import kr.co.amateurs.server.domain.dto.together.MatchPostRequestDTO;
import kr.co.amateurs.server.domain.dto.together.MatchPostResponseDTO;
import kr.co.amateurs.server.domain.entity.post.enums.SortType;
import kr.co.amateurs.server.service.together.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {
    
    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<Page<MatchPostResponseDTO>> getMatchPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") SortType sortType
    ){
        Page<MatchPostResponseDTO> matchList = matchService.getMatchPostList(keyword, page, size, sortType);
        return ResponseEntity.ok(matchList);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchPostResponseDTO> getMatchPost(@PathVariable("matchId") Long matchId){
        MatchPostResponseDTO gatherPost = matchService.getMatchPost(matchId);
        return ResponseEntity.ok(gatherPost);
    }

    @PostMapping
    public ResponseEntity<MatchPostResponseDTO> createMatchPost(@RequestBody MatchPostRequestDTO dto){
        MatchPostResponseDTO post = matchService.createMatchPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<Void> updateMatchPost(@PathVariable("matchId") Long matchId, @RequestBody MatchPostRequestDTO dto){
        matchService.updateMatchPost(matchId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //TODO - Soft Delete 로 변경 시 PATCH 요청으로 변경 예정
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatchPost(@PathVariable("matchId") Long matchId){
        matchService.deleteMatchPost(matchId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
