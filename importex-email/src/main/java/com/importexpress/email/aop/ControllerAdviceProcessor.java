package com.importexpress.email.aop;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jack.luo
 * @date 2019/12/6
 */
@ControllerAdvice
@Slf4j
public class ControllerAdviceProcessor {

    @Autowired
    protected MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult handleException(HttpServletRequest request, Exception ex) {

        int code = CommonResult.FAILED;
        String msg;

        if (ex instanceof BizException) {

            BizException bizException = (BizException) ex;
            msg = bizException.getErrorCode().getDescription();
            log.warn("bizException: " + bizException.getErrorCode());
        } else {
            msg = ex.getMessage();
            log.error("code: " + code + "  msg: " + msg, ex);
        }
        return CommonResult.failed(msg);
    }

}
