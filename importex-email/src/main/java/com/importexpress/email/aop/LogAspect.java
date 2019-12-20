package com.importexpress.email.aop;

import com.importexpress.comm.util.AOPLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * @author jack.luo
 * @date 2019/11/22
 */
@Aspect
@Service
public class LogAspect {


    @Pointcut("execution(* com.importexpress.email.service..*.*(..))")
    public void serviceLog() {
    }


    @Around("serviceLog()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }

    @Pointcut("execution(* com.importexpress.email.mq..*.*(..))")
    public void mqLog() {
    }


    @Around("mqLog()")
    public Object mqAround(ProceedingJoinPoint joinPoint) throws Throwable {

        return AOPLog.watchMethod(joinPoint);
    }
}
