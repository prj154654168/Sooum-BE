package com.sooum.global.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class Pointcuts {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void slackPointcut() {}
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void requesterPointcut() {}
}
