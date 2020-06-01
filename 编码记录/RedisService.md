```java
package cn.peoplevip.miaosha.redis;

import cn.peoplevip.miaosha.Utils.ProtostuffUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 18:40
 */
@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisConfig redisConfig;

    /**
     * 字符串形式存储
     *
     * @param key   字符串键值
     * @param clazz 要转化为的结果类
     * @param <T>   结果类类型
     * @return 返回结果
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String s = jedis.get(realKey);
            return strToBean(s, clazz);
        }
    }

    /**
     * get单个redis数据  以byte[] 存储
     *
     * @param key byte形式的key 示例 “hellop”.getBytes()
     * @return T
     */
    public <T> T get(KeyPrefix prefix, byte[] key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + new String(key);

            byte[] bytes = jedis.get(realKey.getBytes());
            if (bytes != null) {
                return ProtostuffUtil.deserializer(bytes, clazz);
            } else {
                return null;
            }
        }
    }


    /**
     * 字符串形式存储
     */
    public <T> String set(KeyPrefix prefix, String key, T value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return "error,value is empty";
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            //过期时间
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                //永不过期
                return jedis.set(realKey, str);
            } else {
                //原子操作  正常返回ok，错误返回错误信息
                return jedis.setex(realKey, seconds, str);
            }
        }
    }

    /**
     * 存redis单个值 以byte[] 存储
     */
    public <T> String set(KeyPrefix prefix, byte[] key, T value) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (key == null || key.length <= 0) {
                return "error";
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + new String(key);

            //过期时间
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                //永不过期
                return jedis.set(realKey.getBytes(), ProtostuffUtil.serializer(value));
            } else {
                //原子操作  正常返回ok，错误返回错误信息
                return jedis.setex(realKey.getBytes(), seconds, ProtostuffUtil.serializer(value));
            }
        }
    }

    /**
     * 对象到字符串的转化
     * 使用fastjson转化从对象到字符串
     *
     * @param value
     * @param <T>
     * @return
     */
    private <T> String beanToString(T value) {

        if (value == null) {
            return null;
        }
        //return JSON.toJSONString(value);
        Class<?> clazz = value.getClass();

        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * 使用fastjson转化从字符串到对象
     *
     * @param s     字符串
     * @param clazz 对象的类 传参形式 String.class
     * @param <T>
     * @return
     */
    private <T> T strToBean(String s, Class<T> clazz) {
        if (s == null || s.length() <= 0 || clazz == null) {
            return null;
        }
        //return JSON.toJavaObject(JSON.parseObject(s),clazz);
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(s);
        } else if (clazz == String.class) {
            return (T) s;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(s);
        } else {
            return JSON.toJavaObject(JSON.parseObject(s), clazz);
        }
    }


    /**
     * 删除 字符串形式存储数据的删除
     */
    public boolean delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            long ret = jedis.del(key);
            return ret > 0;
        }
    }

    /**
     * 判断key是否存在
     * @param prefix 前缀
     * @param key key
     * @return
     */
    public boolean exists(KeyPrefix prefix,String key){
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }
    }
    /**
     * 判断key是否存在
     * @param prefix 前缀
     * @param key
     * @return
     */
    public boolean exists(KeyPrefix prefix, byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + new String(key);
            return jedis.exists(realKey.getBytes());
        }
    }
    /**
     * 更新redis失效日期
     * 符串形式存储数据的更新
     *
     * @param key
     * @param second
     * @param <T>
     * @return
     */
    public <T> long expire(String key, int second) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, second);
        }
    }

    /**
     * 原子加 String存储
     * 目标+1
     * @param prefix 预定义的键值前缀
     * @param key    键值
     * @return
     */
    public Long incr(KeyPrefix prefix, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }
    }

    /**
     * 原子加  byte[] 存储
     *
     * @param prefix 预定义的键值前缀
     * @param key    键值
     * @return
     */
    public Long incr(KeyPrefix prefix, byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + new String(key);
            return jedis.incr(realKey.getBytes());
        }
    }

    /**
     * 原子🗡减 String存储
     *
     * @param prefix 预定义的键值前缀
     * @param key    键值
     * @return
     */
    public Long decr(KeyPrefix prefix, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }
    }

    /**
     * 原子减  byte[] 存储
     *
     * @param prefix 预定义的键值前缀
     * @param key    键值
     * @return
     */
    public Long decr(KeyPrefix prefix, byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + new String(key);
            return jedis.decr(realKey.getBytes());
        }
    }
}
```

