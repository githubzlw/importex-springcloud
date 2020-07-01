package com.importexpress.comm.pojo;

import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/4/27 16:22
 * Description
 */
@Data
public final class MessageBean {
    private long sender;
    private String msg;
    private long timestamp;
}
