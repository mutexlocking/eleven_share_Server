package com.konkuk.eleveneleven.config;

import com.konkuk.eleveneleven.common.interceptor.AuthInterceptor;
import com.konkuk.eleveneleven.common.interceptor.BeforeAuthInterceptor;
import com.konkuk.eleveneleven.common.interceptor.TimeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/** 인터셉터 등록 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    private final BeforeAuthInterceptor beforeAuthInterceptor;
    private final AuthInterceptor authInterceptor;
    private final TimeInterceptor timeInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        registry.addInterceptor(beforeAuthInterceptor)
                .order(1)
                .addPathPatterns("/auth/email", "/auth/meta", "/ocr", "/auth/login")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth", "/room/**", "/test/**", "/matched/room/url/**");

        registry.addInterceptor(authInterceptor)
                .order(2)
                .addPathPatterns("/room/**", "/matched/room/url/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth", "/auth/login", "/auth/email", "/auth/meta", "/ocr" ,"/test/**");

        registry.addInterceptor(timeInterceptor)
                .order(3)
                .addPathPatterns("/room", "/room/member")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth/**", "/ocr", "/test/**" );
    }
}
