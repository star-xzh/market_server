package com.schoolmarket.market_server.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.schoolmarket.market_server.untils.Re;
import com.schoolmarket.market_server.untils.annotations.PassToken;
import com.schoolmarket.market_server.untils.annotations.UserLoginToken;
import com.schoolmarket.market_server.untils.tools.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

public class LoginInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Method method = handlerMethod.getMethod();

        //检查方法是否有passtoken注解，有则跳过认证，直接通过
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new Re("token为空,请重新登录！");
                }
                // 获取 token 中的 user id
                String openid;
                try {
                    openid = JWT.decode(token).getClaim("openid").asString();
                } catch (JWTDecodeException j) {
                    throw new Re("token不正确，请不要通过非法手段创建token");
                }

                // 验证 token
                if (JwtToken.checkToken(token)) {
                    return true;
                } else {
                    throw new Re("token过期或不正确，请重新登录");
                }

            }
        }
        throw new Re("没有权限注解一律不通过");
    }

}
