package com.green.chakak.chakak.community.repository;

import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyJpaRepository extends JpaRepository<Reply, Long> {

    // 특정 게시글의 활성 댓글 조회 (작성자 정보 포함 - 조인 패치)
    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.post.postId = :postId AND r.status = 'ACTIVE' " +
            "ORDER BY r.createdAt ASC")
    List<Reply> findActiveRepliesByPostIdJoinUser(@Param("postId") Long postId);

    // 특정 게시글의 활성 댓글을 페이징으로 조회
    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.post.postId = :postId AND r.status = 'ACTIVE'")
    Page<Reply> findActiveRepliesByPostIdJoinUser(@Param("postId") Long postId, Pageable pageable);

    // 댓글 상세 조회 (작성자 정보 포함)
    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.replyId = :replyId AND r.status = 'ACTIVE'")
    Optional<Reply> findActiveReplyByIdJoinUser(@Param("replyId") Long replyId);

    // 특정 사용자가 작성한 활성 댓글 조회
    @Query("SELECT r FROM Reply r JOIN FETCH r.post p JOIN FETCH r.user u " +
            "WHERE r.user.userId = :userId AND r.status = 'ACTIVE' " +
            "ORDER BY r.createdAt DESC")
    List<Reply> findActiveRepliesByUserIdJoinPost(@Param("userId") Long userId);

    // 특정 게시글의 활성 댓글 개수 조회
    @Query("SELECT COUNT(r) FROM Reply r WHERE r.post.postId = :postId AND r.status = 'ACTIVE'")
    Long countActiveRepliesByPostId(@Param("postId") Long postId);

    // 특정 사용자의 활성 댓글 개수 조회
    @Query("SELECT COUNT(r) FROM Reply r WHERE r.user.userId = :userId AND r.status = 'ACTIVE'")
    Long countActiveRepliesByUserId(@Param("userId") Long userId);

    // 활성화, 비활성화 모든 댓글 확인
    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.post.postId = :postId " +
            "ORDER BY r.createdAt ASC")
    List<Reply> findAllRepliesByPostIdJoinUser(@Param("postId") Long postId);


    // 게시글별 댓글 존재 여부 확인
    boolean existsByPostAndStatus(Post post, Reply.ReplyStatus status);
}