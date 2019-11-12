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
        if (ex instanceof HttpMessageNotReadableException) {
            code = CommonResult.PARAM_ERROR;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            code = CommonResult.HTTP_METHOD_NOT_SURPPORT;
        }
        String msg = null;


        if (ex instanceof BizException) {
            BizException bizException = (BizException) ex;
            if(BizErrorCodeEnum.EXPIRE_FAIL.equals(bizException.getErrorCode().getCode())){
                log.error("你的授权已经过期！联系万邦");
                msg = bizException.getMessage();
                code = CommonResult.EXPIRE_ERROR;
            }
        } else if (ex instanceof AccessDeniedException) {
            msg = "无权限访问";
            code = CommonResult.FAILED;
        }
        if (msg == null) {
            msg = ex.getMessage();
        }
        CommonResult resp = new CommonResult();
        resp.setCode(code);
        resp.setMessage(msg);
        log.error("code: " + code + "  msg: " + msg, ex);
        return resp;
    }

}
