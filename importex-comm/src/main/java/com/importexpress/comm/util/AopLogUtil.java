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
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        log.info("执行[{}]开始,args:{}", joinPoint.getSignature(),parseParams(joinPoint.getArgs()));
        // 定义返回对象、得到方法需要的参数
        Object obj ;
        long startTime = System.currentTimeMillis();
        try {
            Object[] args = joinPoint.getArgs();
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("统计某方法执行耗时环绕通知出错", e);
            throw e;
        }
        long diffTime = System.currentTimeMillis() - startTime;
        if(log.isDebugEnabled()){
            log.debug("执行[{}]结束,返回值:{},执行耗时:{}ms", joinPoint.getSignature(),obj,diffTime);
        }else{
            log.info("执行[{}]结束,执行耗时:{}ms", joinPoint.getSignature(),diffTime);
        }

        // 打印耗时的信息
        if (diffTime > MAX_TIME ) {
            log.info("执行[{}]结束,返回值:{},执行耗时(超过阈值):{}ms", joinPoint.getSignature(),obj,diffTime);
        }
        return obj;
    }

    /**
     * parse Params
     * @param params params
     * @return str
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
