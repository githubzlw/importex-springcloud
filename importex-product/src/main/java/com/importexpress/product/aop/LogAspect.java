package com.importexpress.product.aop;

import com.importexpress.comm.util.AOPLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * @author jack.luo
 * @date 2019/11/27
 */
@Aspect
@Service
public class LogAspect {

    @Pointcut("execution(* com.importexpress.product.rest..*.*(..))")
    public void controlLog() {
    }

    @Around("controlLog()")
    public Object controlAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.product.service..*.*(..))")
    public void serviceLog() {
    }


    @Around("serviceLog()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }
}
