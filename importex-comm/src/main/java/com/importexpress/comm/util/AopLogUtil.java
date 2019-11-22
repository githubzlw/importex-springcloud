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
    private static final long MAX_TIME = 3000;

    /**
     * 日志记录
     * @param joinPoint joinPoint
     * @return object
     * @throws Throwable
     */
    public static Object watchMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("执行开始[{}],args:{}", joinPoint.getSignature(),parseParams(joinPoint.getArgs()));

        // 定义返回对象、得到方法需要的参数
        Object obj ;
        long startTime = System.currentTimeMillis();
        try {
            Object[] args = joinPoint.getArgs();
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("执行出错", e);
            throw e;
        }
        long diffTime = System.currentTimeMillis() - startTime;
        // 打印耗时的信息
        if (diffTime > MAX_TIME ) {
            log.warn("执行结束[{}],耗时:{}ms", joinPoint.getSignature(),diffTime);
        }else{
            if(log.isDebugEnabled()){
                log.debug("执行结束[{}],耗时:{}ms,返回值:{}", joinPoint.getSignature(),diffTime,obj);
            }else{
                log.info("执行结束[{}],耗时:{}ms", joinPoint.getSignature(),diffTime);
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
