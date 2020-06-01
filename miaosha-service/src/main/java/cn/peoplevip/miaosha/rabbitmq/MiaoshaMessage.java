package cn.peoplevip.miaosha.rabbitmq;


import cn.peoplevip.common.domain.MiaoshaUser;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/5 10:04
 */
public class MiaoshaMessage {
    private MiaoshaUser user;
    private long userId;
    private long goodsId;

    public MiaoshaMessage() {
    }

    public MiaoshaMessage(long userId, long goodsId) {
        this.userId = userId;
        this.goodsId = goodsId;
    }

    public MiaoshaMessage(MiaoshaUser user, long goodsId) {
        this.user = user;
        this.goodsId = goodsId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
