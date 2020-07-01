package com.importexpress.comm.util;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;


/**
 * @author lhao
 * @date 2018/5/15
 */
@Slf4j
public class AOPLog {

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
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Object[] args = joinPoint.getArgs();
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("exec error", e);
            throw e;
        }
        long diffTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        // 打印耗时的信息
        if (diffTime > MAX_TIME ) {
            log.warn("end exec[{}],spend:{}ms,return:{}", joinPoint.getSignature(),diffTime,obj);
        }else{
            log.info("end exec[{}],spend:{}ms,return:{}", joinPoint.getSignature(),diffTime,obj);
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
                if(obj.getClass().isArray()){
                    param.append(ArrayUtils.toString(obj)).append(" ");
                }else{
                    param.append(obj).append(" ");
                }
            }
        }
        return param.toString();
    }

}
