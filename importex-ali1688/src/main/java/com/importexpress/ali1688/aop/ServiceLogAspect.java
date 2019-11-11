package com.importexpress.ali1688.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * @author luohao
 * @date 2019/11/6
 */
@Aspect
@Service
@Slf4j
public class ServiceLogAspect {

    @Pointcut("execution(public * com.importexpress.ali1688.service..*.*(..))")
    public void serivceLog() {
    }

    @Around("serivceLog()")
    public Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return Help.watchMethod(joinPoint);
    }

}
