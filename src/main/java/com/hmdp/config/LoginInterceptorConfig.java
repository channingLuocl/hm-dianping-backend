package com.hmdp.config;

import com.hmdp.security.LoginInterceptor;
import com.hmdp.security.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer {

    //    ①原来只用一个拦截器的代码
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate)).excludePathPatterns(
//                "/shop/***",  //店铺相关的，不拦截
//                "/voucher/**",  //优惠券相关的，不拦截
//                "/shop-type/***",  //查询店铺类型的，不拦截
//                "/upload/***",  //上传图片的，不拦截
//                "/blog/hot",  //查看热点博客的，不拦截
//                "/user/code",  //发验证码的，不拦截
//                "/user/login");  //登录的接口，不拦截
//    }

//    ②改成用两个拦截器的代码

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**");  //拦截所有请求的放在前面，会先执行
        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns(  //拦截需要登录才能访问的请求的放在后面，会后执行
                "/shop/***",
                "/voucher/**",
                "/shop-type/***",
                "/upload/***",
                "/blog/hot",
                "/user/code",
                "/user/login");
    }
}
