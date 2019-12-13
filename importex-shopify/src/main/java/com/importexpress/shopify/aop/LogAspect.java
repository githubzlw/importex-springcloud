package com.importexpress.shopify.aop;

import com.importexpress.comm.util.AOPLog;
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

    @Pointcut("execution(* com.importexpress.shopify.rest..*.*(..))")
    public void controlLog() {
    }

    @Around("controlLog()")
    public Object controlAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.shopify.service..*.*(..))")
    public void serviceLog() {
    }

    @Around("serviceLog()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.shopify.feign..*.*(..))")
    public void feignLog() {
    }

    @Around("feignLog()")
    public Object feignAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }
}
