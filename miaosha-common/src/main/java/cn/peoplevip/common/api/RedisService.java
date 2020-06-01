package cn.peoplevip.common.api;


import cn.peoplevip.common.redisKey.KeyPrefix;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 17:34
 * 功能
 */
public interface RedisService {
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz);
    public <T> String set(KeyPrefix prefix, String key, T value);
    public  <T> String beanToString(T value);
    public <T> T strToBean(String s, Class<T> clazz);
    public boolean delete(KeyPrefix prefix, String key);
    public boolean exists(KeyPrefix prefix, String key);
    public <T> long expire(String key, int second);
    public Long incr(KeyPrefix prefix, String key);
    public Long decr(KeyPrefix prefix, String key);
    public boolean delete(KeyPrefix prefix);
    public List<String> scanKeys(String key);

}
