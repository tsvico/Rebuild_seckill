package cn.peoplevip.other.Service;

import cn.peoplevip.common.Utils.ProtostuffUtil;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.redisKey.KeyPrefix;
import cn.peoplevip.other.redis.RedisConfig;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 18:40
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisConfig redisConfig;

    boolean redisStorage_type;

    // 在方法上加上注解@PostConstruct，这样方法就会在Bean初始化之后被Spring容器执行（注：Bean初始化包括，实例化Bean，并装配Bean的属性（依赖注入））。
    @PostConstruct
    public void init() {
        //true为string方式存储，false为byte[]方式存储
        redisStorage_type = "string".contains(redisConfig.getStorageType());
    }


    /**
     * get单个redis数据  以byte[] 存储/字符串形式存储
     *
     * @param key   字符串键值
     * @param clazz 要转化为的结果类
     * @param <T>   结果类类型
     * @return 返回结果
     */
    @Override
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;

            //这里为了防止不兼容decr
            boolean tempType;
            if (clazz == int.class || clazz == Integer.class) {
                tempType = true;
            } else {
                tempType = redisStorage_type;
            }

            //判断是哪种方式存储
            if (tempType) {
                String s = jedis.get(realKey);
                return strToBean(s, clazz);
            } else {
                //byte方式存储
                byte[] bytes = jedis.get(realKey.getBytes());
                if (bytes != null) {
                    return ProtostuffUtil.deserializer(bytes, clazz);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            Jedis jedis = jedisPool.getResource();
            //出错原因可能是转换出错，全部清空就OK
            jedis.flushAll();
            jedis.close();
            return null;
        }
    }


    /**
     * 字符串形式存储
     */
    @Override
    public <T> String set(KeyPrefix prefix, String key, T value) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            //过期时间
            int seconds = prefix.expireSeconds();
            //这里为了防止不兼容decr
            boolean tempType;
            if (value == null) {
                return null;
            }
            Class<?> clazz = value.getClass();
            if (clazz == int.class || clazz == Integer.class) {
                tempType = true;
            } else {
                tempType = redisStorage_type;
            }
            //判断是哪种方式存储
            if (tempType) {
                String str = beanToString(value);
                if (str == null || str.length() <= 0) {
                    return "error,value is empty";
                }
                if (seconds <= 0) {
                    //永不过期
                    return jedis.set(realKey, str);
                } else {
                    //原子操作  正常返回ok，错误返回错误信息
                    return jedis.setex(realKey, seconds, str);
                }
            } else {
                if (key == null || key.length() <= 0) {
                    return "error,value is empty";
                }
                if (seconds <= 0) {
                    //永不过期
                    return jedis.set(realKey.getBytes(), ProtostuffUtil.serializer(value));
                } else {
                    //原子操作  正常返回ok，错误返回错误信息
                    return jedis.setex(realKey.getBytes(), seconds, ProtostuffUtil.serializer(value));
                }
            }
        }
    }


    /**
     * 对象到字符串的转化
     * 使用fastjson转化从对象到字符串
     *
     * @param value 将要转换的对象
     * @param <T> 对象类
     * @return 转化结果
     */
    @Override
    public <T> String beanToString(T value) {

        if (value == null) {
            return null;
        }
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
     * @param <T>  泛型对象
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T strToBean(String s, Class<T> clazz) {
        if (s == null || s.length() <= 0 || clazz == null) {
            return null;
        }
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
     * 删除 存储数据的删除
     */
    @Override
    public boolean delete(KeyPrefix prefix, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getPrefix() + key;
            //判断是哪种方式存储
            if (redisStorage_type) {
                long ret = jedis.del(realKey);
                return ret > 0;
            } else {
                long ret = jedis.del(realKey.getBytes());
                return ret > 0;
            }
        }
    }

    /**
     * 判断key是否存在
     *
     * @param prefix 前缀
     * @param key    key
     * @return
     */
    @Override
    public boolean exists(KeyPrefix prefix, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            //判断是哪种方式存储
            if (redisStorage_type) {
                return jedis.exists(realKey);
            } else {
                return jedis.exists(realKey.getBytes());
            }
        }
    }


    /**
     * 更新redis失效日期
     * 存储数据的更新
     *
     * @param key
     * @param second
     * @param <T>
     * @return
     */
    @Override
    public <T> long expire(String key, int second) {
        try (Jedis jedis = jedisPool.getResource()) {
            //判断是哪种方式存储
            if (redisStorage_type) {
                return jedis.expire(key, second);
            } else {
                return jedis.expire(key.getBytes(), second);
            }
        }
    }

    /**
     * 原子加
     * 目标+1
     *
     * @param prefix 预定义的键值前缀
     * @param key    键值
     * @return
     */
    @Override
    public Long incr(KeyPrefix prefix, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            //判断是哪种方式存储
            if (redisStorage_type) {
                return jedis.incr(realKey);
            } else {
                return jedis.incr(realKey.getBytes());
            }
        }
    }

    /**
     * 原子🗡减
     *
     * @param prefix 预定义的键值前缀
     * @param key    键值
     * @return
     */
    @Override
    public Long decr(KeyPrefix prefix, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            //判断是哪种方式存储
            if (redisStorage_type) {
                return jedis.decr(realKey);
            } else {
                return jedis.decr(realKey.getBytes());
            }
        }
    }

    @Override
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getCursor();
            }while(!"0".equals(cursor));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
