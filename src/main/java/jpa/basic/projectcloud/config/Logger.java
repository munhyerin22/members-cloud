package jpa.basic.projectcloud.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class Logger {

    @Before("execution(* jpa.basic.projectcloud.user.controller..*.*(..))")
    public void logApiRequest(JoinPoint joinPoint) {
        log.info("[API - LOG] {} 메서드 호출", joinPoint.getSignature().getName());
    }
}
