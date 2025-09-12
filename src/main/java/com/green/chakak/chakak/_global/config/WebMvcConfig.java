package com.green.chakak.chakak._global.config;

import com.green.chakak.chakak._global.argument_resolver.LoginUserArgumentResolver;
import com.green.chakak.chakak._global.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

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
                        "/api/email/verify",
                        "/api/email/send",
                        "/api/v1/users/profile",
                        "/api/photo/services/list",
                        "/api/photo/services/detail/{id}",
                        "/api/photo/categories/list",
                        "/api/photo/mappings/list",
                        "/api/photo/mappings/detail/{id}",
                        "/api/auth/kakao/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }
}
