package com.importexpress.comm.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;


/**
 * @author lhao
 * @date 2018/5/15
 */
@Slf4j
public class AopLogUtil {

    /**
     * 超过此时间显示警告
     */
    private static final long MAX_TIME = 1000;

    /**
     * 日志记录
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    public static Object watchMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        log.info("执行[{}]开始", joinPoint.getSignature().getName());
        log.info(joinPoint.getSignature().toString());
        log.info(parseParams(joinPoint.getArgs()));

        // 定义返回对象、得到方法需要的参数
        Object obj ;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();

        try {
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("统计某方法执行耗时环绕通知出错", e);
            throw e;
        }

        log.info("返回值[{}]", obj!=null ? obj:" NULL ");

        log.info("执行[{}]结束", joinPoint.getSignature().getName());

        long endTime = System.currentTimeMillis();

        // 打印耗时的信息
        long diffTime = endTime - startTime;
        if (diffTime > MAX_TIME ) {
            log.warn("---" + methodName + " 方法执行耗时：" + diffTime + " ms");
            log.warn(joinPoint.getSignature().toString());
            log.warn(parseParams(joinPoint.getArgs()));
            log.warn("返回值[{}]", obj!=null ? obj:" NULL ");
            log.warn("---------------End---------------");
        } else {
            log.info("---" + methodName + " 方法执行耗时：" + diffTime + " ms");
        }

        return obj;
    }

    /**
     * parse Params
     * @param params
     * @return
     */
    private static String parseParams(Object[] params) {
        if (null == params || params.length <= 0) {
            return " NO PARAMS ";
        }
        StringBuilder param = new StringBuilder("Parameters: ");
        for (Object obj : params) {
            if (obj != null) {
                param.append(obj).append(" ");
            } else {
                param.append(" NULL ");
            }
        }
        return param.toString();
    }


}
