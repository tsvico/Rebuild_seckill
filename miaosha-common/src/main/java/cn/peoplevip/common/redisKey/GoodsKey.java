package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 18:00
 * 功能
 */
public class GoodsKey extends BasePrefix {
    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    private GoodsKey(String prefix) {
        super(prefix);
    }
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(600,"gd");
    //商品库存
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey("gs");

}
