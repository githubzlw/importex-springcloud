package com.importexpress.comm.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 通用返回对象
 * @author luohao
 */
@Data
@ApiModel(value = "统一返回接口")
@RequiredArgsConstructor
public class CommonResult {

    /**
     *成功
     */
    public static final int SUCCESS = 200;

    /**
     *失败
     */
    public static final int FAILED = 500;


    @ApiModelProperty(value = "返回状态码；200:成功 ；500:失败")
    private @NonNull   int code;

    @ApiModelProperty(value = "异常消息")
    private String message;

    @ApiModelProperty(value = "成功时的数据")
    private Object data;


    /**
     * 普通成功返回
     *
     */
    public static CommonResult success() {
        CommonResult commonResult = new CommonResult(SUCCESS);
        commonResult.message = "操作成功";
        return commonResult;
    }

    /**
     * 普通成功返回
     *
     * @param data 获取的数据
     */
    public static CommonResult success(Object data) {
        CommonResult commonResult = new CommonResult(SUCCESS);
        commonResult.message = "操作成功";
        commonResult.data = data;
        return commonResult;
    }

    /**
     * 普通成功返回（带message)
     */
    public static CommonResult success(String message,Object data) {
        CommonResult commonResult = new CommonResult(SUCCESS);
        commonResult.message = message;
        commonResult.data = data;
        return commonResult;
    }

      /**
     * 普通失败提示信息
     */
    public static CommonResult failed() {
        CommonResult commonResult = new CommonResult(FAILED);
        commonResult.message = "操作失败";
        return commonResult;
    }

    /**
     * 具体失败提示信息
     * @param message
     * @return
     */
    public static CommonResult failed(String message){
        CommonResult commonResult = new CommonResult(FAILED);
        commonResult.message = message;
        return commonResult;
    }


}
