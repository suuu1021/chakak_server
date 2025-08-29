package com.green.chakak.chakak.account.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserJpaRepository userJpaRepository;

    // 회원 등록
    public User createUser(User user) {
        return userJpaRepository.save(user);
    }

    // 단일 회원 조회
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    // 이메일로 조회
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    // 전체 회원 조회
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userJpaRepository.findAll();
    }

    // 회원 삭제
    public void deleteUser(Long id) {
        if (!userJpaRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userJpaRepository.deleteById(id);
    }

    // 상태 변경 (예: ACTIVE → SUSPENDED)
    public User changeStatus(Long id, UserStatus status) {
        User user = getUser(id);
        user.setStatus(status);
        return user;
    }
}
