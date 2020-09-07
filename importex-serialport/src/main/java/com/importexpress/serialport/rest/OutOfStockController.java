package com.importexpress.serialport.rest;

import com.importexpress.serialport.service.SerialPort2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    // /outOfStock/001/001/001 1号转盘，1号仓库口，数量1
    @GetMapping("/outOfStock/{turnTable}/{box}/{number}")
    @ApiOperation("调用出库指令 outOfStock/001/001/001 1号转盘，1号仓库口，数量1")
    public boolean outOfStock(@PathVariable(name = "turnTable")String turnTable, @PathVariable(name = "box")String box, @PathVariable(name = "number")String number){
        return serialPort2Service.outOfStock(turnTable,box,number);
    }

    @GetMapping("outOfStock/getNearSignal")
    @ApiOperation("获取接近信号：fales 无，true 有")
    public boolean getNearSignal(){
       return serialPort2Service.getNearSignal();
    }

    @GetMapping("outOfStock/getLightSignal")
    @ApiOperation("获取接近信号：fales 无，true 有")
    public boolean getLightSignal(){
        return serialPort2Service.getLightSignal();
    }
    @GetMapping("outOfStock/initStep")
    @ApiOperation("转盘归零：fales 失败，true 成功")
    public boolean initStep(){
        return serialPort2Service.initStep();
    }
    @GetMapping("/moveTurnTable/{steps}/{box}")
    @ApiOperation("移动转盘 /moveTurnTable/{steps}/{box} steps 转盘走多少步，一步0.0045°，最多1w步，超过会不走了，box 推几次")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "steps", value = "转盘走多少步，一步0.0045°，最多1w步，超过会不走了", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "path", name = "box", value = "推杆推几次 0：不推", required = true, dataType = "String"),
    })
    public boolean moveTurnTable(@PathVariable(name = "steps")String steps, @PathVariable(name = "box")String box){
        return serialPort2Service.moveTurnTable(steps,box);
    }

    @GetMapping("/warningLight")
    @ApiOperation("点亮，关闭报警灯")
    @ApiImplicitParam(paramType = "query",name = "state",required = true,dataType = "boolean",value = "打开或者关闭报警灯")
    public boolean warningLight(@RequestParam(name = "state")boolean state){
        return serialPort2Service.warningLight(state);
    }
}
