package kr.co.amateurs.server.service.it;

import kr.co.amateurs.server.domain.common.ErrorCode;
import kr.co.amateurs.server.domain.dto.common.PageResponseDTO;
import kr.co.amateurs.server.domain.dto.common.PaginationSortType;
import kr.co.amateurs.server.domain.dto.common.PostPaginationParam;
import kr.co.amateurs.server.domain.dto.it.ITRequestDTO;
import kr.co.amateurs.server.domain.dto.it.ITResponseDTO;
import kr.co.amateurs.server.domain.dto.post.PostViewedEvent;
import kr.co.amateurs.server.domain.entity.post.ITPost;
import kr.co.amateurs.server.domain.entity.post.Post;
import kr.co.amateurs.server.domain.entity.post.PostStatistics;
import kr.co.amateurs.server.domain.entity.post.enums.BoardType;
import kr.co.amateurs.server.domain.entity.post.enums.SortType;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.domain.entity.user.enums.Role;
import kr.co.amateurs.server.repository.file.PostImageRepository;
import kr.co.amateurs.server.repository.it.ITRepository;
import kr.co.amateurs.server.repository.post.PostRepository;
import kr.co.amateurs.server.repository.post.PostStatisticsRepository;
import kr.co.amateurs.server.service.UserService;
import kr.co.amateurs.server.service.ai.PostEmbeddingService;
import kr.co.amateurs.server.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static kr.co.amateurs.server.domain.dto.common.PageResponseDTO.convertPageToDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ITService {
    private final ITRepository itRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostStatisticsRepository postStatisticsRepository;

    private final UserService userService;
    private final FileService fileService;
    private final PostEmbeddingService postEmbeddingService;

    private final ApplicationEventPublisher eventPublisher;

    public PageResponseDTO<ITResponseDTO> searchPosts(BoardType boardType, PostPaginationParam paginationParam) {
        String keyword = paginationParam.getKeyword();
        Page<ITResponseDTO> itPage;

        if (paginationParam.getField() == PaginationSortType.POST_MOST_VIEW){
            Pageable pageable = PageRequest.of(paginationParam.getPage(), paginationParam.getSize());
            if (keyword != null && !keyword.trim().isEmpty()) {
                itPage = itRepository.findDTOByContentAndBoardTypeOrderByViewCount(keyword.trim(), boardType, pageable);
            }
            else{
                itPage = itRepository.findDTOByBoardTypeOrderByViewCount(boardType, pageable);
            }
        }else{
            Pageable pageable = paginationParam.toPageable();
            if (keyword != null && !keyword.trim().isEmpty()) {
                itPage = itRepository.findDTOByContentAndBoardType(keyword.trim(), boardType, pageable);
            } else {
                itPage = itRepository.findDTOByBoardType(boardType, pageable);
            }
        }


        return convertPageToDTO(itPage);
    }

    public ITResponseDTO getPost(Long itId, String ipAddress) {
        Optional<User> user = userService.getCurrentUser();

        ITResponseDTO result;

        if (user.isPresent()) {
            result = itRepository.findDTOByIdForUser(itId, user.get().getId())
                    .orElseThrow(ErrorCode.NOT_FOUND);
        } else {
            result = itRepository.findDTOByIdForGuest(itId)
                    .orElseThrow(ErrorCode.NOT_FOUND);
        }

        eventPublisher.publishEvent(new PostViewedEvent(result.postId(), ipAddress));

        return result;
    }

    @Transactional
    public ITResponseDTO createPost(ITRequestDTO requestDTO, BoardType boardType) {
        User user = userService.getCurrentLoginUser();

        Post post = Post.from(requestDTO, user, boardType);

        Post savedPost = postRepository.save(post);

        ITPost itPost = ITPost.from(savedPost);
        ITPost savedITPost = itRepository.save(itPost);

        PostStatistics postStatistics = PostStatistics.from(savedPost);
        postStatisticsRepository.save(postStatistics);


        CompletableFuture.runAsync(() -> {
            try {
                postEmbeddingService.createPostEmbeddings(savedPost);
            } catch (Exception e) {
                log.warn("커뮤니티 게시글 임베딩 생성 실패: postId={}", savedPost.getId(), e);
            }
        });

        List<String> imgUrls = fileService.extractImageUrls(requestDTO.content());
        fileService.savePostImage(savedPost, imgUrls);


        return ITResponseDTO.from(savedITPost, false, false);
    }

    @Transactional
    public void updatePost(ITRequestDTO requestDTO, Long postId) {
        ITPost itPost = findById(postId);

        Post post = itPost.getPost();
        validatePost(post);

        post.update(requestDTO);
    }

    @Transactional
    public void deletePost(Long postId) {
        ITPost itPost = findById(postId);

        Post post = itPost.getPost();
        validatePost(post);

        fileService.deletePostImage(post);
        itRepository.delete(itPost);
    }

    private void validatePost(Post post) {
        User user = userService.getCurrentLoginUser();

        if (!canEditOrDelete(post, user)) {
            throw ErrorCode.ACCESS_DENIED.get();
        }
    }

    private boolean canEditOrDelete(Post post, User user) {
        return Objects.equals(post.getUser().getId(), user.getId()) || user.getRole() == Role.ADMIN;
    }

    private ITPost findById(Long itId) {
        return itRepository.findById(itId)
                .orElseThrow(ErrorCode.NOT_FOUND);
    }

    private Pageable createPageable(int page, SortType sortType, int pageSize) {
        Sort sort = switch (sortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCount");
            case VIEW_COUNT -> Sort.by(Sort.Direction.DESC, "viewCount");
        };

        return PageRequest.of(page, pageSize, sort);
    }
}
