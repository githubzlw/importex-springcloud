package com.importexpress.shoppingcart.aop;

import com.importexpress.comm.util.AOPLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * @author luohao
 * @date 2019/12/16
 */
@Aspect
@Service
public class LogAspect {

    @Pointcut("execution(* com.importexpress.shoppingcart.rest..*.*(..))")
    public void controlLog() {
    }

    @Around("controlLog()")
    public Object controlAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.shoppingcart.service..*.*(..))")
    public void serviceLog() {
    }


    @Around("serviceLog()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }
}
