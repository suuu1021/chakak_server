package com.green.chakak.chakak._global.config;

import com.green.chakak.chakak._global.argument_resolver.LoginUserArgumentResolver;
import com.green.chakak.chakak._global.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Value("${file.upload.path:/uploads/portfolios}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**", "/api/v1/**")
                .excludePathPatterns(
                        // 인증 제외(공개) 엔드포인트만 명시
                        "/api/users/login",
                        "/api/users/signup",

                        // 포토그래퍼 목록/검색/지역조회는 공개 처리하되, 개별 상세·me 엔드포인트는 인터셉터 적용
                        "/api/photographers",                  // GET 전체 목록 (공개)
                        "/api/photographers/location/**",     // 지역별 목록 (공개)
                        "/api/photographers/search",          // 검색 (공개)
                        "/api/photographers/create",
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
                        //"/api/photo/services/detail/{id}",
                        "/api/photo/categories/list",
                        "/api/photo/mappings/list",
                        "/api/photo/mappings/detail/{id}",
                        "/api/auth/kakao/**",
                        "/api/payment/success",
                        "/api/payment/fail",
                        "/api/payment/cancel",
                        "/api/admin/login",
                        "/api/photo/mappings/category/*/services"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일에 접근할 수 있도록 정적 리소스 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/../")
                .setCachePeriod(3600); // 1시간 캐시
    }

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
