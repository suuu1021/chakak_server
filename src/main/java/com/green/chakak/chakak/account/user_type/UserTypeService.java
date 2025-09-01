package com.green.chakak.chakak.account.user_type;

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

    // 전체 조회
    public List<UserType> getAllUserTypes() {
        return userTypeRepository.findAll();
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
        }
    }
}