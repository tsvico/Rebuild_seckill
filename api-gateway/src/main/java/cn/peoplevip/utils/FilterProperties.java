package cn.peoplevip.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @date 2020/4/16 14:59
 * @Description xxx
 */
@ConfigurationProperties(prefix = "miaosha.filter")
public class FilterProperties {
    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}
