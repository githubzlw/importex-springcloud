package com.importexpress.shopify.aop;

import com.importexpress.comm.util.AopLogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * @author luohao
 * @date 2019/11/27
 */
@Aspect
@Service
public class LogAspect {

    @Pointcut("execution(* com.importexpress.shopify.control..*.*(..))")
    public void controlLog() {
    }

    @Around("controlLog()")
    public Object controlAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AopLogUtil.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.shopify.service..*.*(..))")
    public void serviceLog() {
    }


    @Around("serviceLog()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AopLogUtil.watchMethod(joinPoint);
    }
}