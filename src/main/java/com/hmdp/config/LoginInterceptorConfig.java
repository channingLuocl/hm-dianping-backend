package com.hmdp.config;

import com.hmdp.security.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
                "/shop/***",  //店铺相关的，不拦截
                "/voucher/**",  //优惠券相关的，不拦截
                "/shop-type/***",  //查询店铺类型的，不拦截
                "/upload/***",  //上传图片的，不拦截
                "/blog/hot",  //查看热点博客的，不拦截
                "/user/code",  //发验证码的，不拦截
                "/user/login");  //登录的接口，不拦截
    }
}
