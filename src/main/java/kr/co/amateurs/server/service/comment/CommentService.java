package kr.co.amateurs.server.service.comment;

import kr.co.amateurs.server.domain.common.ErrorCode;
import kr.co.amateurs.server.domain.dto.comment.CommentPageDTO;
import kr.co.amateurs.server.domain.dto.comment.CommentRequestDTO;
import kr.co.amateurs.server.domain.dto.comment.CommentResponseDTO;
import kr.co.amateurs.server.domain.entity.comment.Comment;
import kr.co.amateurs.server.domain.entity.post.Post;
import kr.co.amateurs.server.domain.entity.user.User;
import kr.co.amateurs.server.exception.CustomException;
import kr.co.amateurs.server.repository.comment.CommentRepository;
import kr.co.amateurs.server.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private static final int CURSOR_OFFSET = 1;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // TODO 사용자가 댓글을 좋아요를 눌렀는지 확인하는 로직 구현 필요

    public CommentPageDTO getCommentsByPostId(Long postId, Long cursor, int size) {
        Post post = findPostById(postId);

        return createCommentPage(
                () -> fetchRootComments(post, cursor, size + CURSOR_OFFSET),
                this::convertToRootCommentDTOs,
                size
        );
    }

    public CommentPageDTO getReplies(Long postId, Long parentCommentId, Long cursor, int size) {
        Comment parentComment = findCommentById(parentCommentId);

        return createCommentPage(
                () -> fetchReplies(parentComment, cursor, size + CURSOR_OFFSET),
                this::convertToReplyDTOs,
                size
        );
    }

    @Transactional
    public CommentResponseDTO createComment(Long postId, CommentRequestDTO requestDTO) {
        // TODO 유저 검증, 게시판 권한 로직
        User user = null;

        Post post = findPostById(postId);

        Comment parentComment = getParentComment(requestDTO.parentCommentId()).orElse(null);

        Comment comment = Comment.from(requestDTO, post, user, parentComment);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDTO.from(savedComment, 0, false);
    }

    @Transactional
    public void updateComment(Long postId, Long commentId, CommentRequestDTO requestDTO) {
        // TODO 유저 검증, 게시판 검증 로직
        User user = null;
        Post post = findPostById(postId);

        Comment comment = findCommentById(commentId);
        // TODO 댓글 유저 같은지 확인 로직 수정 예정
//        if (!comment.getUser().equals(user)) {
//            throw new CustomException(ErrorCode.ACCESS_DENIED);
//        }

        comment.updateContent(requestDTO.content());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        // TODO 유저 검증, 게시판 검증 로직
        User user = null;
        Post post = findPostById(postId);

        Comment comment = findCommentById(commentId);
        // TODO 댓글 유저 같은지 확인 로직 수정 예정

        // TODO soft delete 구현 시 변경
        commentRepository.delete(comment);
    }

    private CommentPageDTO createCommentPage(CommentSupplier commentSupplier, Function<List<Comment>, List<CommentResponseDTO>> converter, int size) {
        List<Comment> comments = commentSupplier.get();

        boolean hasMore = comments.size() > size;
        if (hasMore) {
            comments = comments.subList(0, size);
        }

        Long nextCursor = hasMore && !comments.isEmpty()
                ? comments.get(comments.size() - 1).getId()
                : null;

        List<CommentResponseDTO> commentDTOs = converter.apply(comments);

        return new CommentPageDTO(commentDTOs, nextCursor, hasMore);
    }

    private Optional<Comment> getParentComment(Long parentCommentId) {
        if (parentCommentId == null) {
            return Optional.empty();
        }
        Comment parentComment = findCommentById(parentCommentId);
        if (parentComment.getParentComment() != null) {
            throw new CustomException(ErrorCode.NOT_FOUND); // ErrorCode > 답글에는 답글을 달 수 없습니다.
        }

        return Optional.of(parentComment);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private List<Comment> fetchRootComments(Post post, Long cursor, int size) {
        if (cursor == null) {
            return commentRepository.findByPostAndParentCommentIsNullOrderByCreatedAtAsc(post, PageRequest.of(0, size));
        } else{
            return commentRepository.findByPostAndParentCommentIsNullAndIdGreaterThanOrderByCreatedAtAsc(post, cursor, PageRequest.of(0, size));
        }
    }

    private List<Comment> fetchReplies(Comment parentComment, Long cursor, int size) {
        if (cursor == null) {
            return commentRepository.findByParentCommentOrderByCreatedAtAsc(parentComment, PageRequest.of(0, size));
        } else {
            return commentRepository.findByParentCommentAndIdGreaterThanOrderByCreatedAtAsc(parentComment, cursor, PageRequest.of(0, size));
        }
    }

    private List<CommentResponseDTO> convertToRootCommentDTOs(List<Comment> comments) {
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> replyCountMap = getReplyCountMap(comments);

        return comments.stream()
                .map(comment -> {
                    int replyCount = replyCountMap.getOrDefault(comment.getId(), 0);
                    return CommentResponseDTO.from(comment, replyCount, true);
                })
                .collect(Collectors.toList());
    }

    private List<CommentResponseDTO> convertToReplyDTOs(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentResponseDTO.from(comment, 0, true))
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getReplyCountMap(List<Comment> comments) {
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        Map<Long, Integer> replyCountMap = new HashMap<>();
        commentRepository.countRepliesByParentIds(commentIds)
                .forEach(row -> {
                    Long parentId = (Long) row[0];
                    Long count = (Long) row[1];
                    replyCountMap.put(parentId, count.intValue());
                });

        return replyCountMap;
    }

    @FunctionalInterface
    private interface CommentSupplier {
        List<Comment> get();
    }
}