package com.importexpress.comm.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;


/**
 * @author lhao
 * @date 2018/5/15
 */
@Slf4j
public class AopLogUtil {

    /**
     * 超过此时间显示警告
     */
    private static final long MAX_TIME = 3000;

    /**
     * 日志记录
     * @param joinPoint joinPoint
     * @return object
     * @throws Throwable
     */
    public static Object watchMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("begin exec[{}],args:{}", joinPoint.getSignature(),parseParams(joinPoint.getArgs()));

        // 定义返回对象、得到方法需要的参数
        Object obj ;
        long startTime = System.currentTimeMillis();
        try {
            Object[] args = joinPoint.getArgs();
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("exec error", e);
            throw e;
        }
        long diffTime = System.currentTimeMillis() - startTime;
        // 打印耗时的信息
        if (diffTime > MAX_TIME ) {
            log.warn("end exec[{}],spend:{}ms", joinPoint.getSignature(),diffTime);
        }else{
            if(log.isDebugEnabled()){
                log.debug("end exec[{}],spend:{}ms,return:{}", joinPoint.getSignature(),diffTime,obj);
            }else{
                log.info("end exec[{}],spend:{}ms", joinPoint.getSignature(),diffTime);
            }
        }

        return obj;
    }

    /**
     * parse Params
     * @param params params
     * @return str
     */
    private static String parseParams(Object[] params) {
        StringBuilder param = new StringBuilder();
        for (Object obj : params) {
            if (obj != null) {
                param.append(obj).append(",");
            }
        }
        return param.toString();
    }

}
