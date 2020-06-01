package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 18:00
 * 功能
 */
public class UserKey extends BasePrefix {
    private UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    private UserKey(String prefix) {
        super(prefix);
    }
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
    public static UserKey getkaptcha = new UserKey(60*2,"kaptcha");

}
