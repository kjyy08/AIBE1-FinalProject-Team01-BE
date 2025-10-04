package kr.co.amateurs.server.domain.like.service;

import kr.co.amateurs.server.domain.ai.model.dto.PostContentData;
import kr.co.amateurs.server.common.model.dto.ErrorCode;
import kr.co.amateurs.server.common.model.dto.PageResponseDTO;
import kr.co.amateurs.server.common.model.dto.PaginationParam;
import kr.co.amateurs.server.domain.like.model.dto.LikeResponseDTO;
import kr.co.amateurs.server.domain.post.model.dto.PostResponseDTO;
import kr.co.amateurs.server.domain.comment.model.entity.Comment;
import kr.co.amateurs.server.domain.like.model.entity.Like;
import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.user.model.entity.User;
import kr.co.amateurs.server.common.exception.CustomException;
import kr.co.amateurs.server.domain.comment.repository.CommentRepository;
import kr.co.amateurs.server.domain.like.repository.LikeRepository;
import kr.co.amateurs.server.domain.post.repository.PostJooqRepository;
import kr.co.amateurs.server.domain.post.repository.PostRepository;
import kr.co.amateurs.server.domain.user.repository.UserRepository;
import kr.co.amateurs.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

import static kr.co.amateurs.server.common.model.dto.PageResponseDTO.convertPageToDTO;
import static kr.co.amateurs.server.domain.like.model.dto.LikeResponseDTO.convertToDTO;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    private final PostJooqRepository postJooqRepository;

    public PageResponseDTO<PostResponseDTO> getLikePostList(PaginationParam paginationParam) {
        User user = userService.getCurrentLoginUser();
        Pageable pageable = paginationParam.toPageable();

        Page<PostResponseDTO> postResponseDTO = postJooqRepository.findPostsByType(user.getId(), pageable, "liked")
                .map(PostResponseDTO::applyBlindFilter);

        return convertPageToDTO(postResponseDTO);
    }

    @Transactional
    public LikeResponseDTO addLikeToPost(Long postId) {
        User currentUser = userService.getCurrentLoginUser();
        Post post = postRepository.findById(postId).orElseThrow(ErrorCode.NOT_FOUND);
        if(checkHasLiked(postId, currentUser.getId())) {
            throw ErrorCode.DUPLICATE_LIKE.get();
        }

        Like likeToPost = Like.builder()
                .user(currentUser)
                .post(post)
                .build();
        Like savedLike = likeRepository.save(likeToPost);

        post.incrementLikeCount();

        return convertToDTO(savedLike, "post");
    }

    @Transactional
    public LikeResponseDTO addLikeToComment(Long postId, Long commentId) {
        User currentUser = userService.getCurrentLoginUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.NOT_FOUND);

        validateCommentBelongsToPost(comment, postId);
        if (checkCommentHasLiked(commentId, currentUser.getId())){
            throw ErrorCode.DUPLICATE_LIKE.get();
        }

        Like likeToPost = Like.builder()
                .user(currentUser)
                .comment(comment)
                .build();
        Like savedLike = likeRepository.save(likeToPost);

        comment.incrementLikeCount();

        return convertToDTO(savedLike, "comment");

    }

    @Transactional
    public void removeLikeFromPost(Long postId) {
        User currentUser = userService.getCurrentLoginUser();
        Post post = postRepository.findById(postId).orElseThrow(ErrorCode.NOT_FOUND);

        if(!checkHasLiked(postId, currentUser.getId())) {
            throw ErrorCode.NOT_FOUND.get();
        }

        likeRepository.deleteByPostIdAndUserId(postId, currentUser.getId());

        post.decrementLikeCount();
    }

    @Transactional
    public void removeLikeFromComment(Long postId, Long commentId) {
        User currentUser = userService.getCurrentLoginUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.NOT_FOUND);

        validateCommentBelongsToPost(comment, postId);
        if(!checkCommentHasLiked(commentId, currentUser.getId())) {
            throw ErrorCode.NOT_FOUND.get();
        }

        likeRepository.deleteByCommentIdAndUserId(commentId, currentUser.getId());

        comment.decrementLikeCount();
    }

    public boolean checkHasLiked(Long postId, Long userId) {
        return likeRepository
                .existsByPost_IdAndUser_Id(postId, userId);
    }

    public boolean checkCommentHasLiked(Long commentId, Long userId) {
        return likeRepository
                .existsByComment_IdAndUser_Id(commentId, userId);
    }

    public List<PostContentData> getLikedPosts(Long userId) {
        try {
            List<Like> likes = likeRepository.findTop3ByUserIdAndPostIsNotNullOrderByCreatedAtDesc(userId);
            return likes.stream().map(like ->
                    new PostContentData(like.getPost().getId(), like.getPost().getTitle(), like.getPost().getContent(), "좋아요")).toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void validateCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPostId().equals(postId)) {
            throw new CustomException(ErrorCode.INVALID_COMMENT_POST_RELATION);
        }
    }

    public boolean hasRecentLikeActivity(Long userId, int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            return likeRepository.existsByUserIdAndPostIsNotNullAndCreatedAtAfter(userId, since);
        } catch (Exception e) {
            return false;
        }
    }
}
