package com.sooum.global.aop.aspect;

import com.sooum.global.slack.dto.RequestDto;
import com.sooum.global.slack.service.SlackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Arrays;
import java.util.Objects;

@Profile("prod")
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SlackAspect {
    private final SlackService slackService;

    @Before("com.sooum.global.aop.pointcut.Pointcuts.slackPointcut()")
    public void before(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object[] args = joinPoint.getArgs();
        if (isNotParamCond(args)) {
            return;
        }

        if (request instanceof ContentCachingRequestWrapper requestWrapper) {
            Exception e = getException(args);
            RequestDto requestDto = new RequestDto(requestWrapper);
            slackService.sendSlackErrorMsg(e, requestDto);
        }
    }

    private boolean isNotParamCond(Object[] classParams) {
        return isNotExistParams(classParams) || isNotExistExceptionParam(classParams);
    }

    private boolean isNotExistParams(Object[] classParams) {
        return Objects.isNull(classParams) || classParams.length == 0;
    }

    private boolean isNotExistExceptionParam(Object[] classParams) {
        return Arrays.stream(classParams).noneMatch(p -> p instanceof Exception);
    }

    private Exception getException(Object[] classParams) {
        for (Object param : classParams) {
            if (param instanceof Exception) {
                return (Exception) param;
            }
        }
        return null;
    }
}
