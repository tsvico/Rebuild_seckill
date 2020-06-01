package cn.peoplevip.miaosha.Bean;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/5 11:05
 * 标记秒杀是否结束/库存量全局Bean
 */
@Component
public class LocalOverMap {
    private Map<Long, Boolean> localOverMap;

    public LocalOverMap() {
        this.localOverMap = new HashMap<>();
    }

    public Map<Long, Boolean> getLocalOverMap() {
        return localOverMap;
    }

    public void setLocalOverMap(Map<Long, Boolean> localOverMap) {
        this.localOverMap = localOverMap;
    }

    /**
     * 设置库存余量
     * @param id 商品ID
     * @param islocalOver 库存余量
     */
    public void put(Long id,Boolean islocalOver){
        localOverMap.put(id,islocalOver);
    }
    public Boolean get(Long id){
        return localOverMap.get(id);
    }
}
