package com.importexpress.message.service;

import com.importexpress.comm.pojo.MessageBean;
import com.importexpress.comm.pojo.SiteEnum;

import java.util.List;

/**
 * @author jack.luo
 */
public interface OneToOneMsgService {

    /**
     * 发送一对一消息
     * @param site
     * @param fromUserId
     * @param toUserId
     * @param msg
     */
    void sendMsg(SiteEnum site, long fromUserId, long toUserId,String msg);

    /**
     * 读取消息，并且保存消息到redis队列中
     * @param site
     * @param userId
     * @return
     */
    List<MessageBean> readMsg(SiteEnum site, long userId);

}
