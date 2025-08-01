package kr.co.amateurs.server.controller.together;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.amateurs.server.domain.dto.common.PageResponseDTO;
import kr.co.amateurs.server.domain.dto.together.MarketPostResponseDTO;
import kr.co.amateurs.server.domain.dto.together.MarketPostRequestDTO;
import kr.co.amateurs.server.domain.dto.common.PostPaginationParam;
import kr.co.amateurs.server.domain.entity.post.enums.GatheringStatus;
import kr.co.amateurs.server.domain.entity.post.enums.MarketStatus;
import kr.co.amateurs.server.service.together.MarketService;
import kr.co.amateurs.server.utils.ClientIPUtils;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
@Tag(name="Market", description = "함께해요 게시판의 장터 탭 API")
public class MarketController {
    private final MarketService marketService;

    @GetMapping
    @Operation(summary = "장터 글 리스트", description = "장터 탭의 모든 게시글을 검색어, 정렬기준에 따라 불러옵니다.")
    public ResponseEntity<PageResponseDTO<MarketPostResponseDTO>> getMarketPostList(
            @ParameterObject @Valid PostPaginationParam paginationParam
            ){
        PageResponseDTO<MarketPostResponseDTO> marketList = marketService.getMarketPostList(paginationParam);
        return ResponseEntity.ok(marketList);
    }

    @GetMapping("/{marketId}")
    @Operation(summary = "장터 글 정보", description = "장터 탭의 특정 게시글의 정보를 불러옵니다.")
    public ResponseEntity<MarketPostResponseDTO> getMarketPost(@PathVariable("marketId") Long marketId, HttpServletRequest request) {
        String ipAddress = ClientIPUtils.getClientIP(request);
        MarketPostResponseDTO gatherPost = marketService.getMarketPost(marketId, ipAddress);
        return ResponseEntity.ok(gatherPost);
    }

    @PostMapping
    @Operation(summary = "장터 글쓰기", description = "장터 탭에 새로운 게시글을 등록합니다.")
    public ResponseEntity<MarketPostResponseDTO> createMarketPost(
            @RequestBody @Valid MarketPostRequestDTO dto){
        MarketPostResponseDTO post = marketService.createMarketPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PutMapping("/{marketId}")
    @Operation(summary = "장터 글 수정", description = "장터 탭의 본인이 작성한 게시글을 수정합니다.")
    public ResponseEntity<Void> updateMarketPost(
            @PathVariable("marketId") Long marketId,
            @RequestBody @Valid MarketPostRequestDTO dto){
        marketService.updateMarketPost(marketId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{marketId}/{status}")
    @Operation(summary = "장터 글 상태 수정", description = "장터 탭의 본인이 작성한 게시글의 상태를 수정합니다.")
    public ResponseEntity<Void> updateGatheringPostStatus(
            @PathVariable("marketId") Long marketId,
            @PathVariable("status") MarketStatus status
    ){
        marketService.updateMarketPostStatus(marketId, status);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //TODO - Soft Delete 로 변경 시 PATCH 요청으로 변경 예정
    @DeleteMapping("/{marketId}")
    @Operation(summary = "장터 글 삭제", description = "장터 탭의 본인이 작성한 게시글을 삭제합니다.")
    public ResponseEntity<Void> deleteMarketPost(
            @PathVariable("marketId") Long marketId){
        marketService.deleteMarketPost(marketId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
