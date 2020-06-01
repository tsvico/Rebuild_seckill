package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 18:00
 * 功能
 */
public class MiaoshaUserKey extends BasePrefix {
    //1天
    public static final int TOKEN_EXPIRE = 3600*24*1;
    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");
    //只要User未发生变化，希望永久有效
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"Id");

}
