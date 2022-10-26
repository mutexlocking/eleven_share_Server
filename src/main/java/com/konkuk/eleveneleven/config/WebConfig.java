package com.konkuk.eleveneleven.config;

import com.konkuk.eleveneleven.common.interceptor.AuthInterceptor;
import com.konkuk.eleveneleven.common.interceptor.TimeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/** 인터셉터 등록 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final TimeInterceptor timeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth/login", "/test/**", "/auth");

        registry.addInterceptor(timeInterceptor)
                .order(2)
                .addPathPatterns("/room", "/room/member")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth/login", "/auth/email", "/test/**" );
    }
}
