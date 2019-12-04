package com.importexpress.utils.aop;

import com.importexpress.comm.util.AopLogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author luohao
 * @date 2019/11/22
 */
@Aspect
@Service
public class LogAspect {

    @Pointcut("execution(* com.importexpress.utils.rest..*.*(..))")
    public void controlLog() {
    }

    @Around("controlLog()")
    public Object controlAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AopLogUtil.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.utils.service..*.*(..))")
    public void serviceLog() {
    }


    @Around("serviceLog()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AopLogUtil.watchMethod(joinPoint);
    }
}
