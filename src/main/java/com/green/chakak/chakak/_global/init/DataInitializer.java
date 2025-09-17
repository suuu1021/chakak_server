package com.green.chakak.chakak._global.init;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.admin.domain.Admin;
import com.green.chakak.chakak.admin.service.repository.AdminJpaRepository;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserTypeRepository userTypeRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final AdminJpaRepository adminJpaRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // UserType은 Builder가 없으므로 new와 setter를 사용합니다.
        UserType userRole = userTypeRepository.findByTypeCode("user").orElseGet(() -> {
            UserType newUserType = new UserType();
            newUserType.setTypeCode("user");
            newUserType.setTypeName("일반회원");
            newUserType.setCreatedAt(LocalDateTime.now());
            newUserType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newUserType);
        });

        UserType photographerRole = userTypeRepository.findByTypeCode("photographer").orElseGet(() -> {
            UserType newPhotographerType = new UserType();
            newPhotographerType.setTypeCode("photographer");
            newPhotographerType.setTypeName("사진작가");
            newPhotographerType.setCreatedAt(LocalDateTime.now());
            newPhotographerType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newPhotographerType);
        });

        UserType adminRole = userTypeRepository.findByTypeCode("admin").orElseGet(() -> {
            UserType newAdminType = new UserType();
            newAdminType.setTypeCode("admin");
            newAdminType.setTypeName("관리자");
            newAdminType.setCreatedAt(LocalDateTime.now());
            newAdminType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newAdminType);
        });
//

            // 1. 일반 유저 및 프로필 10개 생성
        for (int i = 1; i <= 10; i++) {
            // User 생성 (평문 비밀번호 저장으로 원상복귀)
            User newUser = User.builder()
                    .email(String.format("user%d@example.com", i))
                    .password("123456") // 평문 비밀번호 저장
                    .userType(userRole)
                    .status(User.UserStatus.ACTIVE)
                    .emailVerified(true)
                    .build();
            userJpaRepository.save(newUser);

            // UserProfile 생성
            UserProfile userProfile = UserProfile.builder()
                    .user(newUser)
                    .nickName(String.format("일반유저%d", i))
                    .introduce(String.format("안녕하세요, 유저 %d입니다.", i))
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .build();
            userProfileJpaRepository.save(userProfile);
        }

        // 2. 사진작가 유저 및 프로필 10개 생성
        for (int i = 1; i <= 10; i++) {
            // User 생성 (평문 비밀번호 저장으로 원상복귀)
            User newPhotographerUser = User.builder()
                    .email(String.format("photo%d@example.com", i))
                    .password("123456") // 평문 비밀번호 저장
                    .userType(photographerRole)
                    .status(User.UserStatus.ACTIVE)
                    .emailVerified(true)
                    .build();
            userJpaRepository.save(newPhotographerUser);

            // PhotographerProfile 생성
            PhotographerProfile photographerProfile = PhotographerProfile.builder()
                    .user(newPhotographerUser)
                    .businessName(String.format("감성스튜디오%d호점", i))
                    .introduction(String.format("최고의 순간을 담아드립니다. 감성스튜디오 %d입니다.", i))
                    .status("ACTIVE")
                    .location("서울")
                    .experienceYears(i)
                    .build();
            photographerRepository.save(photographerProfile);
        }

        Admin newAdmin = Admin.builder()
                .adminName("superadmin")
                .password("super1234")
                .userType(adminRole)
                .build();
        adminJpaRepository.save(newAdmin);
    }
}
