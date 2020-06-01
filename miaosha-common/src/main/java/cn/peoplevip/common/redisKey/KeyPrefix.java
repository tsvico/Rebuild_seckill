package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 9:50
 * Redis接口
 */
public interface KeyPrefix {
    /**
     * 有效期
     * @return
     */
    public int expireSeconds();
    //前缀
    public String getPrefix();
}
