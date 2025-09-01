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
}