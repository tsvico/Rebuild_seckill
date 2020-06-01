package cn.peoplevip.common.domain;

import cn.peoplevip.common.Utils.JsonLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/27 11:28
 */
public class MiaoshaOrder implements Serializable {
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long userId;
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long orderId;
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long goodsId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("userId", userId)
                .append("orderId", orderId)
                .append("goodsId", goodsId)
                .toString();
    }
}
