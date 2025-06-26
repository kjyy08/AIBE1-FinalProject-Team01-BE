package kr.co.amateurs.server.service.bookmark;

import kr.co.amateurs.server.domain.common.ErrorCode;
import kr.co.amateurs.server.domain.dto.bookmark.BookmarkResponseDTO;
import kr.co.amateurs.server.domain.dto.common.PageResponseDTO;
import kr.co.amateurs.server.domain.dto.common.PaginationParam;
import kr.co.amateurs.server.domain.entity.bookmark.Bookmark;
import kr.co.amateurs.server.domain.entity.post.GatheringPost;
import kr.co.amateurs.server.domain.entity.post.MarketItem;
import kr.co.amateurs.server.domain.entity.post.MatchingPost;
import kr.co.amateurs.server.domain.entity.post.Post;
import kr.co.amateurs.server.domain.entity.post.enums.BoardType;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.domain.entity.user.enums.Role;
import kr.co.amateurs.server.exception.CustomException;
import kr.co.amateurs.server.repository.bookmark.BookmarkRepository;
import kr.co.amateurs.server.repository.post.PostRepository;
import kr.co.amateurs.server.repository.together.GatheringRepository;
import kr.co.amateurs.server.repository.together.MarketRepository;
import kr.co.amateurs.server.repository.together.MatchRepository;
import kr.co.amateurs.server.repository.user.UserRepository;
import kr.co.amateurs.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.co.amateurs.server.domain.dto.bookmark.BookmarkResponseDTO.*;
import static kr.co.amateurs.server.domain.dto.common.PageResponseDTO.convertPageToDTO;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final GatheringRepository gatheringRepository;
    private final MarketRepository marketRepository;
    private final MatchRepository matchRepository;

    private final UserService userService;

    public PageResponseDTO<BookmarkResponseDTO> getBookmarkPostList(Long userId, PaginationParam paginationParam) {
        validateUser(userId);
        Pageable pageable = paginationParam.toPageable();
        Page<Bookmark> bookmarkList = switch(paginationParam.getField()){
            case LATEST -> bookmarkRepository.getBookmarkPostByUser(userId, pageable);
            case POPULAR -> bookmarkRepository.getBookmarkPostByUserOrderByLikeCountDesc(userId, pageable);
            case MOST_VIEW -> bookmarkRepository.getBookmarkPostByUserOrderByViewCountDesc(userId, pageable);
            default -> bookmarkRepository.getBookmarkPostByUser(userId, pageable);
        };
        return convertPageToDTO(bookmarkList.map(this::convertToDTO));
    }

    public BookmarkResponseDTO addBookmarkPost(Long userId, Long postId) {
        validateUser(userId);
        User currentUser = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        Bookmark newBookmark = Bookmark.builder()
                .user(currentUser)
                .post(post)
                .build();
        Bookmark savedBookmark = bookmarkRepository.save(newBookmark);
        return convertToDTO(savedBookmark);
    }

    @Transactional
    public void removeBookmarkPost(Long userId, Long postId) {
        validateUser(userId);
        bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
    }

    private BookmarkResponseDTO convertToDTO(Bookmark bookmark) {
        Post p = bookmark.getPost();
        Long postId = p.getId();
        BoardType boardType = p.getBoardType();
        return switch (boardType){
            case GATHER -> {
                GatheringPost gp = gatheringRepository.findByPostId(postId);
                yield convertToGatheringDTO(bookmark, gp);
            }
            case MARKET -> {
                MarketItem mi = marketRepository.findByPostId(postId);
                yield convertToMarketDTO(bookmark, mi);
            }
            case MATCH -> {
                MatchingPost mp = matchRepository.findByPostId(postId);
                yield convertToMatchingDTO(bookmark, mp);
            }
            default -> convertToPostDTO(bookmark);
        };
    }
    public boolean checkHasBookmarked(Long postId) {
        User user = userService.getCurrentUser().get();
        return bookmarkRepository
                .findByPostIdAndUserId(postId, user.getId())
                .isPresent();
    }

    private void validateUser(Long userId) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        Long currentId = user.get().getId();
        Role currentRole = user.get().getRole();

        if (!currentId.equals(userId) && currentRole != Role.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "본인의 북마크에만 접근할 수 있습니다.");
        }
    }
}
