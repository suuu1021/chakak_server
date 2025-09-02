package com.green.chakak.chakak.account.user_type;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/user-types")
public class UserTypeController {

    private final UserTypeService userTypeService;

    public UserTypeController(UserTypeService userTypeService) {
        this.userTypeService = userTypeService;
    }

    @PostConstruct
    public void init() {
        userTypeService.initializeUserTypes();
    }

    // 전체 조회
    @GetMapping
    public List<UserType> getAllUserTypes() {
        return userTypeService.getAllUserTypes();
    }

    // 상세 보기
    @GetMapping("/{id}")
    public UserType getUserType(@PathVariable Long id) {
        return userTypeService.getUserTypeById(id);
    }

    // 등록
    @PostMapping
    public UserType createUserType(@RequestBody UserTypeRequest request){
        return userTypeService.createUserType(request);
    }

    // 수정
    @PutMapping("/{id}")
    public UserType updateUserType(@PathVariable Long id, @RequestBody UserTypeRequest request) {

        return userTypeService.updateUserType(id, request);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deleteUserType(@PathVariable Long id){
        userTypeService.deleteUserType(id);
    }
}