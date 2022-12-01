package com.konkuk.eleveneleven.config;

import com.konkuk.eleveneleven.common.interceptor.AuthInterceptor;
import com.konkuk.eleveneleven.common.interceptor.BeforeAuthInterceptor;
import com.konkuk.eleveneleven.common.interceptor.FirstAuthInterceptor;
import com.konkuk.eleveneleven.common.interceptor.TimeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/** 인터셉터 등록 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    private final FirstAuthInterceptor firstAuthInterceptor;
    private final BeforeAuthInterceptor beforeAuthInterceptor;
    private final AuthInterceptor authInterceptor;
    private final TimeInterceptor timeInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(firstAuthInterceptor)
                        .order(1)
                        .addPathPatterns("/auth")
                        .excludePathPatterns("/css/**", "/*.ico", "/error","/auth/email", "/auth/meta", "/auth/login",
                                "/room/**", "/matched/room/url/**", "/auth/quit","/room/member");

        registry.addInterceptor(beforeAuthInterceptor)
                .order(2)
                .addPathPatterns("/auth/email", "/auth/meta", "/auth/login")
                .excludePathPatterns("/css/**", "/*.ico", "/error",  "/room/**", "/test/**", "/matched/room/url/**", "/ocr", "/auth/quit", "/auth");

        registry.addInterceptor(authInterceptor)
                .order(3)
                .addPathPatterns("/room/**", "/matched/room/url/**", "/auth/quit", "/matched/room/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth", "/auth/login", "/auth/email", "/auth/meta", "/ocr" ,"/test/**");

        registry.addInterceptor(timeInterceptor)
                .order(4)
                .addPathPatterns("/room", "/room/member")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/auth/**", "/ocr", "/test/**", "/matched/**");
    }
}
