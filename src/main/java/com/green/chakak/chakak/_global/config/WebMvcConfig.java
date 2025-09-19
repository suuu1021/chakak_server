package com.green.chakak.chakak._global.config;

import com.green.chakak.chakak._global.argument_resolver.LoginUserArgumentResolver;
import com.green.chakak.chakak._global.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
                        "/api/photographers/*",
                        "/api/photographers/location/**",
                        "/api/photographers/search",
                        "/api/photographer-categories/**",
                        "/api/portfolios",
                        "/api/portfolios/search",
                        "/api/portfolios/photographer/**",
                        "/api/portfolio-categories",
                        "/api/portfolio-categories/*",
                        "/api/email/verify",
                        "/api/email/send",
                        "/api/v1/users/profile",
                        "/api/photo/services/list",
                        "/api/photo/services/detail/{id}",
                        "/api/photo/categories/list",
                        "/api/photo/mappings/list",
                        "/api/photo/mappings/detail/{id}",
                        "/api/auth/kakao/**",
                        "/api/photo/mappings/detail/{id}",
                        "/api/payment/success",
                        "/api/payment/fail",
                        "/api/payment/cancel",
                        "/api/photo/mappings/detail/{id}",
                        "/api/admin/login",
                        "/api/photo/mappings/category/*/services"
                        );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("Authorization", "Content-Type")
                    .exposedHeaders("Custom-Header")
                    .maxAge(3600);
        }
    }
}
