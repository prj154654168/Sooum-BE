package com.sooum.global.aop.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Profile("test")
@Aspect
@Component
@RequiredArgsConstructor
public class RequesterInfoAspect {

    @Before("com.sooum.global.aop.pointcut.Pointcuts.requesterPointcut() || com.sooum.global.aop.pointcut.Pointcuts.slackPointcut()")
    public void before() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        log.info("[Request URI]: {}, [User-Agent]: {}, [Client IP]: {}",
                request.getRequestURI(),
                request.getHeader("User-Agent"),
                ip);
    }
}
