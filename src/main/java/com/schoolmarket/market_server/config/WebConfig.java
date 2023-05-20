package com.schoolmarket.market_server.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry res){
        res.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(exclude());
        WebMvcConfigurer.super.addInterceptors(res);
    }


    public String[] exclude() {
        return new String[]{
                "/user/login",
        };
    }

}
