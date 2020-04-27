package com.importexpress.message.service.impl;

import com.google.gson.Gson;
import com.importexpress.comm.pojo.MessageBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.message.service.OneToOneMsgService;
import com.importexpress.message.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author jack.luo
 */
@Slf4j
@Service
public class OneToOneMsgServiceImpl implements OneToOneMsgService {

    public static final int MAX_DAYS_EXPIRE = 90;

    private final Config config;

    private final StringRedisTemplate redisTemplate;


    public OneToOneMsgServiceImpl(StringRedisTemplate redisTemplate, Config config) {
        this.config = config;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送一对一消息
     * @param site
     * @param fromUserId
     * @param toUserId
     * @param msg
     */
    @Override
    public void sendMsg(SiteEnum site, long fromUserId, long toUserId,String msg) {

        String key = getUnReadMessageKey(site,toUserId);

        MessageBean messageBean = new MessageBean();
        messageBean.setMsg(msg);
        messageBean.setSender(fromUserId);
        messageBean.setTimestamp(System.currentTimeMillis());
        String json = new Gson().toJson(messageBean, MessageBean.class);
        this.redisTemplate.opsForList().leftPush(key, json);
        this.redisTemplate.expire(key, MAX_DAYS_EXPIRE, TimeUnit.DAYS);

    }

    /**
     * 读取消息，并且保存消息到redis队列中
     * @param site
     * @param userId
     * @return
     */
    @Override
    public List<MessageBean> readMsg(SiteEnum site, long userId) {

        String key = getUnReadMessageKey(site,userId);
        String saveKey = getReadedMessageKey(site,userId);

        //read message from redis
        List<MessageBean> lstMessageBean = new ArrayList<>();
        Gson gson = new Gson();
        String value;
        MessageBean bean;
        while (true) {
            value = this.redisTemplate.opsForList().rightPop(key);
            if (StringUtils.isNotEmpty(value)) {
                bean = gson.fromJson(value, MessageBean.class);
                lstMessageBean.add(bean);
            }else{
                break;
            }
        }

        if(!lstMessageBean.isEmpty()) {
            //save message to redis
            List<String> lstJson = new ArrayList<>();
            lstMessageBean.forEach(item -> lstJson.add(gson.toJson(item, MessageBean.class)));
            this.redisTemplate.opsForList().leftPushAll(saveKey, lstJson);
        }

        return lstMessageBean;
    }

    /**
     * get UnReadMessageKey
     * @param site
     * @param id
     * @return
     */
    private String getUnReadMessageKey(SiteEnum site, long id) {
        return config.MESSAGE_PRE + ':' + site.toString().substring(0, 1).toLowerCase() + ":mailbox:" + id;
    }

    /**
     * get ReadedMessageKey
     * @param site
     * @param id
     * @return
     */
    private String getReadedMessageKey(SiteEnum site, long id) {
        return config.MESSAGE_PRE + ':' + site.toString().substring(0, 1).toLowerCase() + ":mailboxrd:" + id;
    }
}
