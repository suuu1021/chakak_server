package com.green.chakak.chakak.account.service;

import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.account.service.request.UserTypeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserTypeService {

    private final UserTypeRepository userTypeRepository;

    public UserTypeService(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    // 초기값 삽입
    @Transactional
    public void initializeUserTypes() {
        if (userTypeRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            UserType personal = new UserType();
            personal.setTypeName("개인회원");
            personal.setTypeCode("user");
            personal.setCreatedAt(now);
            personal.setUpdatedAt(now);
            userTypeRepository.save(personal);

            UserType photographer = new UserType();
            photographer.setTypeName("포토그래퍼");
            photographer.setTypeCode("photographer");
            photographer.setCreatedAt(now);
            photographer.setUpdatedAt(now);
            userTypeRepository.save(photographer);

            UserType admin = new UserType();
            admin.setTypeName("관리자");
            admin.setTypeCode("admin");
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            userTypeRepository.save(admin);
        }
    }

    // 전체 조회
    public List<UserType> getAllUserTypes() {
        return userTypeRepository.findAll();
    }

    // 상세 조회
    public UserType getUserTypeById(Long id) {
        return userTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 회원 유형이 없습니다."));
    }


    // 등록
    @Transactional
    public UserType createUserType(UserTypeRequest request) {

        UserType userType = new UserType();
        LocalDateTime now = LocalDateTime.now();

        userType.setTypeName(request.getTypeName());
        userType.setTypeCode(request.getTypeCode());
        userType.setCreatedAt(now);
        userType.setUpdatedAt(now);

        return userTypeRepository.save(userType);
    }

    // 수정
    @Transactional
    public UserType updateUserType(Long id, UserTypeRequest request){
        UserType userType = userTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 회원 유형이 존재하지 않습니다."));

                userType.setTypeName(request.getTypeName());
                userType.setTypeCode(request.getTypeCode());
                userType.setUpdatedAt(LocalDateTime.now());

                return userTypeRepository.save(userType);

    }

    // 삭제
    public void deleteUserType(Long id){
        userTypeRepository.deleteById(id);
    }
}