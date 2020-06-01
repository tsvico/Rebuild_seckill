package cn.peoplevip.common.redisKey;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 9:58
 */
public class BasePrefix implements KeyPrefix {

    private int expireSeconds;
    private String prefix;

    @Override
    public int expireSeconds() {
        //默认0代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

    public BasePrefix(int expireSeconds, String prefix) {
        //防止缓存雪崩，随机时间存储
        this.expireSeconds = expireSeconds + (int) (Math.random() * 11 + 40);
        this.prefix = prefix;
    }

    public BasePrefix(int expireSeconds, String prefix,String noRandom) {
        //特殊情况需要去掉随机时间，原来参数基础上随便传参就可以
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        //代表永不过期
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

}
