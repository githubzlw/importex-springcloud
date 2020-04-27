package com.importexpress.message.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.MessageBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.message.service.OneToOneMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author jack.luo
 * @date 2019/12/25
 */
@RestController
@Slf4j
@Api("一对一消息发送")
@RequestMapping("/msg/one2one")
public class OneToOneMsgControl {


    private OneToOneMsgService oneToOneMsgService;

    @Autowired
    public OneToOneMsgControl(OneToOneMsgService messageService) {

        this.oneToOneMsgService = messageService;
    }

    @PostMapping("/{site}")
    @ApiOperation("发送一对一消息")
    public CommonResult sendMsg(@PathVariable(value = "site") SiteEnum site,
                                    @RequestParam long fromUserId,
                                    @RequestParam long toUserId,@RequestParam String msg) {

        try{
            oneToOneMsgService.sendMsg(site, fromUserId, toUserId, msg);
            return CommonResult.success();
        }catch (Exception e){
            log.error("sendMsg",e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/{site}/{userId}")
    @ApiOperation("读取用户消息")
    public CommonResult readMsg(@PathVariable(value = "site") SiteEnum site,
                                @PathVariable(value = "userId") long userId) {

        try{
            List<MessageBean> messageBeans = oneToOneMsgService.readMsg(site, userId);
            return CommonResult.success(messageBeans);
        }catch (Exception e){
            log.error("readMsg",e);
            return CommonResult.failed(e.getMessage());
        }

    }

}
