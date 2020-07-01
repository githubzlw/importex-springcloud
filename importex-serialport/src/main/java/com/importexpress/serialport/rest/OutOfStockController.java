package com.importexpress.serialport.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.service.SerialPort2Service;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * *****************************************************************************************
 *
 * @ClassName OutOfStockController
 * @Author: cjc
 * @Descripeion 转盘出库
 * @Date： 2020/7/1 1:28 下午
 * @Version 1.0
 * <p>
 * <p>
 * Version    Date                ModifiedBy                 Content
 * --------   ---------           ----------                -----------------------
 * 1.0.0       1:28 下午2020/7/1     cjc                       初版
 * ******************************************************************************************
 */
@Api("仓库出库")
@RestController
@RequestMapping("/warehouse")
@Slf4j
public class OutOfStockController {

    @Autowired
    SerialPort2Service serialPort2Service;

    @GetMapping("/outOfStock/{turnTable}/{box}/{number}")
    @ApiOperation("调用出库指令")
    public CommonResult outOfStock(@PathVariable(name = "turnTable")int turnTable, @PathVariable(name = "box")int box, @PathVariable(name = "number")int number){
        StringBuffer appenUrl = new StringBuffer();
        appenUrl.append(turnTable);
        appenUrl.append("_");
        appenUrl.append(box);
        appenUrl.append("=");
        appenUrl.append(number);
        try{
            serialPort2Service.sendData(String.valueOf(appenUrl));
            return CommonResult.success();
        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
}
