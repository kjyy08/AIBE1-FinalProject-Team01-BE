package kr.co.amateurs.server.domain.bookmark.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.amateurs.server.common.annotation.checkpostmetadata.CheckPostMetaData;
import kr.co.amateurs.server.domain.bookmark.model.dto.BookmarkResponseDTO;
import kr.co.amateurs.server.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "북마크 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @CheckPostMetaData
    @PostMapping("/bookmarks/{postId}")
    @Operation(summary = "북마크 등록", description = "특정 게시글을 북마크에 등록합니다.")
    public ResponseEntity<BookmarkResponseDTO> addBookmarkPost(
            @PathVariable Long postId
    ){
         BookmarkResponseDTO bookmarkPost = bookmarkService.addBookmarkPost(postId);
         return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkPost);
    }

    @CheckPostMetaData
    @DeleteMapping("/bookmarks/{postId}")
    @Operation(summary = "북마크 제거", description = "북마크 해둔 특정 게시글의 북마크를 해제합니다.")
    public ResponseEntity<Void> removeBookmarkPost(
            @PathVariable Long postId
    ){
        bookmarkService.removeBookmarkPost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}