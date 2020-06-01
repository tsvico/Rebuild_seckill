package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 18:00
 * 功能
 */
public class OrderKey extends BasePrefix {
    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    private OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("miaoorder");
    public static OrderKey getMiaoshaOrderInfoByUidGid = new OrderKey(2000,"miaoorderInfo");

}
