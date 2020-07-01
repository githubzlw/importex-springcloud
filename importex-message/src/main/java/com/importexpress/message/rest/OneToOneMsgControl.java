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


    private final OneToOneMsgService oneToOneMsgService;

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

    @GetMapping("/{site}/{userId}/unread")
    @ApiOperation("读取用户未读消息")
    public CommonResult readUnReadMsg(@PathVariable(value = "site") SiteEnum site,
                                @PathVariable(value = "userId") long userId) {

        try{
            List<MessageBean> messageBeans = oneToOneMsgService.readUnReadMsg(site, userId);
            return CommonResult.success(messageBeans);
        }catch (Exception e){
            log.error("readUnReadMsg",e);
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/{site}/{userId}/readed")
    @ApiOperation("读取用户已读消息")
    public CommonResult readReadedMsg(@PathVariable(value = "site") SiteEnum site,
                                      @PathVariable(value = "userId") long userId) {

        try{
            List<MessageBean> messageBeans = oneToOneMsgService.readReadedMsg(site, userId);
            return CommonResult.success(messageBeans);
        }catch (Exception e){
            log.error("readReadedMsg",e);
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/{site}/{userId}/rmunread")
    @ApiOperation("删除未读消息")
    public CommonResult removeUnReadMsg(@PathVariable(value = "site") SiteEnum site,
                                      @PathVariable(value = "userId") long userId,@RequestParam int count) {

        try{
            return CommonResult.success(oneToOneMsgService.removeUnReadMsg(site, userId,count));
        }catch (Exception e){
            log.error("removeUnReadMsg",e);
            return CommonResult.failed(e.getMessage());
        }

    }
}
