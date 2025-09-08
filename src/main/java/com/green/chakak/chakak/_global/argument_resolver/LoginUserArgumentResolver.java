package com.green.chakak.chakak._global.argument_resolver;

import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 호출된 컨트롤러 메서드의 파라미터에 LoginUser 타입이 있는지,
        // (여기서는 LoginUser 타입만 확인)
        return parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // supportsParameter가 true를 반환했을 때만 실행됨
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // LoginInterceptor에서 request attribute에 저장해 둔 LoginUser 객체를 꺼내서 반환
        return request.getAttribute(Define.LOGIN_USER);
    }
}
