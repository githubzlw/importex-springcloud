package com.importexpress.comm.exception;

/**
 * 错误码接口
 * @author luohao
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getDescription();

}