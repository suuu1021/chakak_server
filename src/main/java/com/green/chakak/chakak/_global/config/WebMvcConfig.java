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
                        "/api/photographers/{photographerId}",
                        "/api/photographers/search",
                        "/api/photographers/location/**",
                        "/api/photographers/user/{userId}",
                        "/api/photographer-categories/**",
                        "/api/portfolios/**",
                        "/api/email/verify",
                        "/api/email/send"
                );
    }
}
