package com.hmdp.security;

import com.hmdp.dto.UserDTO;
import com.hmdp.utils.UserHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//拦截器
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("拦截器中的preHandle");
//        1.从request中获取session
        HttpSession session = request.getSession();
//        2.获取session中的用户
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
//        3.判断用户是否存在
        if (userDTO == null) {
//            4.如果用户不存在，拦截,返回401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
//        5.存在，则把用户信息放到ThreadLocal，用到com.hmdp.utils.UserHolder工具类
        UserHolder.saveUser(userDTO);
//        6.放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("拦截器中的postHandle");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("拦截器中的afterCompletion");
        UserHolder.removeUser();
    }
}
