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


    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.post.postId = :postId AND r.status = 'ACTIVE' " +
            "ORDER BY r.createdAt ASC")
    List<Reply> findActiveRepliesByPostIdJoinUser(@Param("postId") Long postId);


    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.post.postId = :postId AND r.status = 'ACTIVE'")
    Page<Reply> findActiveRepliesByPostIdJoinUser(@Param("postId") Long postId, Pageable pageable);


    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.replyId = :replyId AND r.status = 'ACTIVE'")
    Optional<Reply> findActiveReplyByIdJoinUser(@Param("replyId") Long replyId);


    @Query("SELECT r FROM Reply r JOIN FETCH r.post p JOIN FETCH r.user u " +
            "WHERE r.user.userId = :userId AND r.status = 'ACTIVE' " +
            "ORDER BY r.createdAt DESC")
    List<Reply> findActiveRepliesByUserIdJoinPost(@Param("userId") Long userId);


    @Query("SELECT COUNT(r) FROM Reply r WHERE r.post.postId = :postId AND r.status = 'ACTIVE'")
    Long countActiveRepliesByPostId(@Param("postId") Long postId);


    @Query("SELECT COUNT(r) FROM Reply r WHERE r.user.userId = :userId AND r.status = 'ACTIVE'")
    Long countActiveRepliesByUserId(@Param("userId") Long userId);


    @Query("SELECT r FROM Reply r JOIN FETCH r.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE r.post.postId = :postId " +
            "ORDER BY r.createdAt ASC")
    List<Reply> findAllRepliesByPostIdJoinUser(@Param("postId") Long postId);


    boolean existsByPostAndStatus(Post post, Reply.ReplyStatus status);
}