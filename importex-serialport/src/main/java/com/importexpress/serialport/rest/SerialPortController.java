package com.importexpress.serialport.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.bean.GoodsBean;
import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.service.SerialPortService;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api(tags = "串口通信")
public class SerialPortController {

    private final SerialPortService serialPortService;



    public SerialPortController(SerialPortService serialPortService) {
        this.serialPortService = serialPortService;

    }

    @GetMapping("/sendData")
    @ApiOperation("发送字符串数据到串口（远程调用）")
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
    @ApiOperation("发送xyz数据到串口（调试用）")
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
    @ApiOperation("回到零点（调试用）")
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
    @ApiOperation("当前位置设为零位（调试用）")
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
    @ApiOperation("释放物品（消磁）（调试用）")
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
    @ApiOperation("吸取物品（吸磁）（调试用）")
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

//    @GetMapping("/moveToCart")
//    @ApiOperation("移动到托盘并且释放掉物品")
//    public CommonResult moveToCart() {
//
//        try{
//            serialPortService.moveToCart();
//            return CommonResult.success();
//
//        }catch (NoSuchPortException ise){
//            return CommonResult.failed("No Such Port");
//        }catch (PortInUseException ise){
//            return CommonResult.failed("Port In Use");
//        }catch (Exception e){
//            return CommonResult.failed(e.getMessage());
//        }
//    }

    @GetMapping("/moveGoods")
    @ApiOperation("移动物品到托盘区并且释放,再回到零点（调试用）")
    public CommonResult moveGoods(@RequestParam int x,@RequestParam int y,@RequestParam int z,@RequestParam String goodsId) {

        try{
            serialPortService.moveGoods(x,y,z,goodsId);
            return CommonResult.success();

        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (SerialPortException spe){
            throw spe;
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

    @GetMapping("/getAllGoods")
    @ApiOperation("读取指定日期的库存列表（远程调用）")
    public CommonResult getAllGoods(@RequestParam String yyyyMMdd) {

        try {
            String json = serialPortService.getAllGoodsFromJsonFile(yyyyMMdd);
            return CommonResult.success(json);
        } catch (IOException e) {
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/moveGoodsByFinder")
    @ApiOperation("商品出库操作（远程调用） sample: 20200619144110070:1-1,20200624093705854:1_2,20200628132548686:1_5")
    public CommonResult moveGoodsByFinder(@RequestParam String params) {

        Map<String, String> map = ofMap(params);

        try {
            return CommonResult.success(serialPortService.moveGoodsByFinder(map));
        }catch (NoSuchPortException ise){
            return CommonResult.failed("No Such Port");
        }catch (PortInUseException ise){
            return CommonResult.failed("Port In Use");
        }catch (SerialPortException spe){
            throw spe;
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/returnMoveGoodsByFinder")
    @ApiOperation("出库商品再入库（远超调用）")
    public CommonResult returnMoveGoodsByFinder(@RequestParam String turnTable,@RequestParam String box,@RequestParam String goodsId) throws PortInUseException, NoSuchPortException {

        try {
            serialPortService.returnMoveGoodsByFinder(turnTable, box, goodsId);
            return CommonResult.success();
        } catch (IOException | InterruptedException | UnsupportedCommOperationException e) {
            return CommonResult.failed(e.getMessage());
        } catch (PortInUseException | NoSuchPortException e) {
            throw e;
        }
    }

    private Map<String, String> ofMap(@RequestParam String params) {
        String[] split = params.split(",");
        Map<String, String> map = new HashMap<>(split.length);
        for (String str : split) {
            String[] kv = str.split(":");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    @GetMapping("/readLight")
    @ApiOperation("读取光电信号（调试用）")
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

    @GetMapping("/cancelFindAllGoodsByGrid")
    @ApiOperation("地毯式扫描中途取消")
    public CommonResult cancelFindAllGoodsByGrid() {

        return CommonResult.success(serialPortService.cancelFindAllGoodsByGrid());
    }

    @GetMapping("/findAllGoodsByGrid")
    @ApiOperation("地毯式扫描货物（调试用），进行入库操作准备")
    public CommonResult findAllGoodsByGrid() {

        try {
            List<GoodsBean> lstGoodsBean = serialPortService.findAllGoodsByGrid();
            return CommonResult.success(lstGoodsBean);

        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/readGoodsId")
    @ApiOperation("条形码读取（调试用）")
    public CommonResult readGoodsId() {

        try {
            return CommonResult.success(serialPortService.readScan());

        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }

    }

}
