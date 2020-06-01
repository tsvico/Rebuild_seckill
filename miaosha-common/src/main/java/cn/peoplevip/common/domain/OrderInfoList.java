package cn.peoplevip.common.domain;

import java.io.Serializable;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/9 11:58
 * 用户购物车列表展示
 */
public class OrderInfoList extends OrderInfo implements Serializable {
    private String goodImg;

    public String getGoodImg() {
        return goodImg;
    }

    public void setGoodImg(String goodImg) {
        this.goodImg = goodImg;
    }
}
