package com.green.chakak.chakak._global.interceptor;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.green.chakak.chakak._global.errors.exception.Exception401;
import com.green.chakak.chakak._global.errors.exception.Exception500;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak._global.utils.JwtUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component // IoC 대상 (싱글톤 패턴으로 관리)
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * preHandle - 컨트롤러에 들어 가기 전에 동작 하는 메서드이다.
     * 리턴 타입이 boolean 이라서 true ---> 컨트롤러 안으로 들어간다, false --> 못 들어 감
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        log.debug("==== JWT 인증 인터셉터 시작 ====");
        String jwt = request.getHeader(Define.AUTH);
        // Bearer + 공백
        if (jwt == null || !jwt.startsWith(Define.BEARER)) {
            throw new Exception401("JWT 토큰을 전달 해주세요.");
        }
        jwt = jwt.replace(Define.BEARER, "");

        try {
            
            if (jwt != null) {
                String userTypeName = JwtUtil.getUserTypeName(jwt);
                log.info("Decoded userTypeName from token: '{}'", userTypeName); // 디버깅용 로그 추가
                if ("admin".equals(userTypeName)) {
                    LoginAdmin loginAdmin = JwtUtil.verifyAdmin(jwt);
                    request.setAttribute(Define.LOGIN_ADMIN, loginAdmin);

                } else {
                    LoginUser loginUser = JwtUtil.verify(jwt);
                    request.setAttribute(Define.LOGIN_USER, loginUser);

                    }
                }

            return true; // 다음 단계로 진행


        } catch (TokenExpiredException e) {
            throw new Exception401("토큰 만료 시간이 지났습니다. 다시 로그인 해주세요.");
        } catch (JWTDecodeException e) {
            throw new Exception401("토큰이 유효하지 않습니다.");
        } catch (Exception e) {
            log.error("Unhandled exception in LoginInterceptor", e); // 에러 로깅 개선
            throw new Exception500(e.getMessage());
        }
    }

    //뷰가 렌더링 되기전에 콜백 되는 메서드
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    // 뷰가 완전 렌더링 된 후 호출 될 수 있다.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
