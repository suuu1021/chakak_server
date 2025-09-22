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


    Optional<Like> findByPostAndUser(Post post, User user);


    @Query("SELECT l FROM Like l WHERE l.post.postId = :postId AND l.user.userId = :userId")
    Optional<Like> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);


    Long countByPost(Post post);


    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);


    @Query("SELECT l FROM Like l JOIN FETCH l.post p JOIN FETCH p.user " +
            "WHERE l.user.userId = :userId ORDER BY l.createdAt DESC")
    List<Like> findByUserIdJoinPost(@Param("userId") Long userId);


    @Query("SELECT l FROM Like l JOIN FETCH l.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE l.post.postId = :postId ORDER BY l.createdAt DESC")
    List<Like> findByPostIdJoinUser(@Param("postId") Long postId);


    @Query("SELECT COUNT(l) FROM Like l WHERE l.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);


    boolean existsByPostAndUser(Post post, User user);


    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l " +
            "WHERE l.post.postId = :postId AND l.user.userId = :userId")
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}