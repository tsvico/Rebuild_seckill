package cn.peoplevip.common.vo;


import cn.peoplevip.common.Utils.JsonLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/10 23:53
 */
public class OrderInfoVo {
    //订单ID
    private int id;
    //下单用户
    private String username;
    //订单号
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long orderId;
    //订单创建时间
    private Date createDate;
    //下单价格
    private Double goodsPrice;
    //订单状态 未支付、已支付、已发货、已收货
    private int status;
    //下单渠道
    private int orderChannel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(int orderChannel) {
        this.orderChannel = orderChannel;
    }
}
