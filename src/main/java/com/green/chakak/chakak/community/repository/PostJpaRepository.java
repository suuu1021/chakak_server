package com.green.chakak.chakak.community.repository;

import com.green.chakak.chakak.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    // 활성 상태인 게시글만 조회 (삭제되지 않은 글)
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile WHERE p.status = 'ACTIVE'")
    List<Post> findAllActiveWithUser();

    // 활성 상태인 게시글을 페이지네이션으로 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile WHERE p.status = 'ACTIVE'")
    Page<Post> findAllActiveWithUser(Pageable pageable);

    // 게시글 상세 조회 (조인 패치 적용)
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile LEFT JOIN FETCH u.userType WHERE p.postId = :id AND p.status = 'ACTIVE'")
    Optional<Post> findActiveByIdWithUser(@Param("id") Long id);

    // 제목이나 내용에 키워드가 포함된 활성 게시글 검색
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE p.status = 'ACTIVE' AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findActiveByKeywordWithUser(@Param("keyword") String keyword, Pageable pageable);

    // 사용자 타입별 활성 게시글 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile LEFT JOIN FETCH u.userType ut " +
            "WHERE p.status = 'ACTIVE' AND ut.typeCode = :userType")
    Page<Post> findActiveByUserTypeWithUser(@Param("userType") String userType, Pageable pageable);

    // 키워드와 사용자 타입으로 활성 게시글 검색
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile LEFT JOIN FETCH u.userType ut " +
            "WHERE p.status = 'ACTIVE' AND ut.typeCode = :userType AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findActiveByKeywordAndUserTypeWithUser(@Param("keyword") String keyword,
                                                      @Param("userType") String userType,
                                                      Pageable pageable);

    // 특정 사용자가 작성한 활성 게시글 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE p.status = 'ACTIVE' AND u.userId = :userId")
    List<Post> findActiveByUserIdWithUser(@Param("userId") Long userId);

    // 조회수가 많은 인기 게시글 조회 (상위 N개)
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE p.status = 'ACTIVE' ORDER BY p.viewCount DESC")
    List<Post> findTopActiveByViewCount(Pageable pageable);

    // 활성 게시글 개수 조회
    @Query("SELECT COUNT(p) FROM Post p WHERE p.status = 'ACTIVE'")
    Long countActivePosts();

    // 특정 기간 내 작성된 활성 게시글 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.user u LEFT JOIN FETCH u.userProfile " +
            "WHERE p.status = 'ACTIVE' AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Post> findActiveByCreatedAtBetweenWithUser(@Param("startDate") java.sql.Timestamp startDate,
                                                    @Param("endDate") java.sql.Timestamp endDate);
}