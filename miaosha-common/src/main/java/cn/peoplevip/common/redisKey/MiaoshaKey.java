package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 18:00
 * 功能
 */
public class MiaoshaKey extends BasePrefix {
    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    private MiaoshaKey(String prefix) {
        super(prefix);
    }
    public static MiaoshaKey isGoodsOver = new MiaoshaKey("gl");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");

}
