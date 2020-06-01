package cn.peoplevip.common.domain;

import cn.peoplevip.common.Utils.JsonLongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/27 11:16
 * 秒杀商品
 */
public class MiaoshaGoods implements Serializable {
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
