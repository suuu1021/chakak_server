package com.green.chakak.chakak.community.repository;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.community.domain.Like;
import com.green.chakak.chakak.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

    // 특정 게시글과 사용자의 좋아요 조회
    Optional<Like> findByPostAndUser(Post post, User user);

    // 특정 게시글과 사용자ID로 좋아요 조회
    @Query("SELECT l FROM Like l WHERE l.post.postId = :postId AND l.user.userId = :userId")
    Optional<Like> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    // 특정 게시글의 좋아요 개수 조회
    Long countByPost(Post post);

    // 특정 게시글ID의 좋아요 개수 조회
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);

    // 특정 사용자가 누른 좋아요 목록 조회 (게시글 정보 포함)
    @Query("SELECT l FROM Like l JOIN FETCH l.post p JOIN FETCH p.user " +
            "WHERE l.user.userId = :userId ORDER BY l.createdAt DESC")
    List<Like> findByUserIdJoinPost(@Param("userId") Long userId);

    // 특정 게시글의 좋아요 목록 조회 (사용자 정보 포함)
    @Query("SELECT l FROM Like l JOIN FETCH l.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE l.post.postId = :postId ORDER BY l.createdAt DESC")
    List<Like> findByPostIdJoinUser(@Param("postId") Long postId);

    // 특정 사용자의 좋아요 개수 조회
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // 특정 게시글과 사용자 조합의 좋아요 존재 여부 확인
    boolean existsByPostAndUser(Post post, User user);

    // 특정 게시글ID와 사용자ID 조합의 좋아요 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l " +
            "WHERE l.post.postId = :postId AND l.user.userId = :userId")
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}