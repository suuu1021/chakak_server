package com.green.chakak.chakak._global.config;

import com.green.chakak.chakak._global.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**") // 1. /api/ 로 시작하는 모든 경로에 인터셉터 적용
                .excludePathPatterns(       // 2. 그 중에서 아래 경로들은 제외
                        "/api/users/login",
                        "/api/users/signup",
                        "/api/photographers",
                        "/api/photographers/detail/{photographerId}",
                        "/api/photographers/search",
                        "/api/photographers/location/**",
                        "/api/photographers/user/{userId}",
                        "/api/photographer-categories/**",
                        "/api/portfolios/**",
                        // --- 충돌 해결: 두 사람의 코드를 모두 반영 ---
                        "/api/email/verify",
                        "/api/email/send",
                        "/api/v1/users/profile",
                        "/api/photo/services",
                        "/api/photo/services/{id}",
                        "/api/photo/categories",
                        "/api/photo/categories/{id}",
                        "/api/photo/mappings",
                        "/api/photo/mappings/{id}"
                );
    }
}
