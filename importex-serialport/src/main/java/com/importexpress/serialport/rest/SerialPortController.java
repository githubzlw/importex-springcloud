package com.importexpress.serialport.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.service.SerialPortService;
import com.importexpress.serialport.util.Config;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api(tags = "串口通信")
public class SerialPortController {

    private final SerialPortService serialPortService;

    private final Config config;

    public SerialPortController(SerialPortService serialPortService, Config config) {
        this.serialPortService = serialPortService;
        this.config = config;
    }

    @GetMapping("/sendData")
    @ApiOperation("发送字符串数据到串口")
    public CommonResult sendData(@RequestParam String msg) {

        try{
            serialPortService.sendData(msg);
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/sendDataXYZ")
    @ApiOperation("发送xyz数据到串口")
    public CommonResult sendData(@RequestParam int x,@RequestParam int y,@RequestParam int z,@RequestParam boolean isMagi) {

        try{
            serialPortService.sendData(x,y,z,isMagi);
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/returnZeroPosi")
    @ApiOperation("普通的回到零点指令")
    public CommonResult returnZeroPosi() {

        try{
            serialPortService.returnZeroPosi();
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/setZeroPosi")
    @ApiOperation("当前位置设为零位")
    public CommonResult setZeroPosi() {

        try{
            serialPortService.setZeroPosi();
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/execMagoff")
    @ApiOperation("释放物品（消磁）")
    public CommonResult execMagoff(@RequestParam int x,@RequestParam int y,@RequestParam int z) {

        try{
            serialPortService.execMagoff(x,y,z);
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/execMagNet")
    @ApiOperation("吸取物品（吸磁）")
    public CommonResult execMagNet(@RequestParam int x,@RequestParam int y,@RequestParam int z) {

        try{
            serialPortService.execMagNet(x,y,z);
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/moveToCart")
    @ApiOperation("移动到托盘并且释放掉物品")
    public CommonResult moveToCart() {

        try{
            serialPortService.moveToCart();
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/moveGoods")
    @ApiOperation("移动物品到托盘区并且释放,再回到零点")
    public CommonResult moveGoods(@RequestParam int x,@RequestParam int y,@RequestParam int z) {

        try{
            serialPortService.moveGoods(x,y,z);
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/closeSerial")
    @ApiOperation("关闭串口（长时间不用需要关闭）,isReturnZero=true(归零)")
    public CommonResult closeSerial(@RequestParam(value = "isReturnZero", required = false, defaultValue = "false") boolean isReturnZero) {

        try{
            if(isReturnZero){
                serialPortService.returnZeroPosi();
            }
            serialPortService.closeSerial();
            return CommonResult.success();
        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/moveGoodsByFinder")
    @ApiOperation("移动货物 sample: 20200619144110070:1_1,20200624093705854:1_2,20200628132548686:1_5")
    public CommonResult moveGoodsByFinder(@RequestParam String params) {

        String[] split = params.split(",");
        Map<String, String> map = new HashMap<>(split.length);
        for(String str : split){
            String[] kv = str.split(":");
            if(kv.length == 2){
                map.put(kv[0], kv[1]);
            }
        }
        return CommonResult.success(serialPortService.moveGoodsByFinder(map));

    }


    @GetMapping("/getAllGoods")
    @ApiOperation("读取指定日期的库存列表")
    public CommonResult getAllGoods(@RequestParam String yyyyMMdd) {

        try {
            String json = serialPortService.getAllGoodsFromJsonFile(yyyyMMdd);
            return CommonResult.success(json);
        } catch (IOException e) {
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/readLight")
    @ApiOperation("读取光电信号")
    public CommonResult readLight(@RequestParam int x,@RequestParam int y,@RequestParam int z) {

        try {
            if(serialPortService.readLight(x,y,z)){
                return CommonResult.success(true);
            }else{
                return CommonResult.success(false);
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }

    }

}
