package com.importexpress.ali1688.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "pid_queue")
public class PidQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shop_id")
    private String shopId;

    private Integer pid;

    /**
     * 0:初始  1:已执行 2:多次执行
     */
    private Byte status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return shop_id
     */
    public String getShopId() {
        return shopId;
    }

    /**
     * @param shopId
     */
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    /**
     * @return pid
     */
    public Integer getPid() {
        return pid;
    }

    /**
     * @param pid
     */
    public void setPid(Integer pid) {
        this.pid = pid;
    }

    /**
     * 获取0:初始  1:已执行 2:多次执行
     *
     * @return status - 0:初始  1:已执行 2:多次执行
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置0:初始  1:已执行 2:多次执行
     *
     * @param status 0:初始  1:已执行 2:多次执行
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}