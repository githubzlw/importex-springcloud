package com.importexpress.ali1688.aop;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizErrorCodeEnum;
import com.importexpress.comm.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

/**
 * @author luohao
 * @date 2019/11/12
 */
@ControllerAdvice
@Slf4j
public class ControllerAdviceProcessor {

    @Autowired
    protected MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult handleException(HttpServletRequest request, Exception ex) {

        int code = CommonResult.SYSTEM_ERROR;
        String msg;

        if (ex instanceof BizException) {

            BizException bizException = (BizException) ex;

            code = bizException.getErrorCode().getCode();
            msg = bizException.getErrorCode().getDescription();
        }else{
            msg = ex.getMessage();
        }

        CommonResult resp = new CommonResult();
        resp.setCode(code);
        resp.setMessage(msg);
        log.error("code: " + code + "  msg: " + msg, ex);
        return resp;
    }

}
