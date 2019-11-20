package com.importexpress.ali1688.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;

/**
 * @author luohao
 * @date 2019/11/6
 */
@Aspect
@Service
@Slf4j
public class LogAspect {

    @Before("execution(* com.importexpress.ali1688.service..*.*(..))")
    public void beforeService(JoinPoint joinPoint){
        //Advice
        log.info("do before execution joinPoint: {}", joinPoint);
    }

    @AfterReturning(value = "execution(* com.importexpress.ali1688.service..*.*(..))",
            returning = "result")
    public void afterReturningService(JoinPoint joinPoint, Object result) {
        log.info("do after {} returned with value joinPoint:{}", joinPoint, result);
    }

    @Before("execution(* com.importexpress.ali1688.control..*.*(..))")
    public void beforeControl(JoinPoint joinPoint){
        //Advice
        log.info("do before execution joinPoint: {}", joinPoint);
    }

    @AfterReturning(value = "execution(* com.importexpress.ali1688.control..*.*(..))",
            returning = "result")
    public void afterReturningControl(JoinPoint joinPoint, Object result) {
        log.info("do after {} returned with value joinPoint:{}", joinPoint, result);
    }

//    @After(value = "execution(* com.importexpress.fx.service..*.*(..))")
//    public void after(JoinPoint joinPoint) {
//        log.info("do after execution joinPoint: {}", joinPoint);
//    }

}
