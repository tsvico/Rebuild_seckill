package cn.peoplevip.other.config;


import cn.peoplevip.other.access.AccessInterceptor;
import cn.peoplevip.other.access.AdminInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/26 22:42
 * 拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

    @Autowired
    AdminInterceptor adminInterceptor;

    //@Value("${frontEndHost}")
    //String frontHost;

    /**
     * 拦截用户登录
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    /**
     * 拦截请求次数
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
        //拦截非管理员
        registry.addInterceptor(adminInterceptor).
                addPathPatterns("/admin/**").
                excludePathPatterns("/admin/login");
    }

    /**
     * 添加跨域 添加拦截器后此方法失效
     * @param registry
     */
    //@Override
//    public void addCorsMappings(CorsRegistry registry) {
//        log.info("允许跨域的域名为:"+frontHost);
//        registry.addMapping("/**")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedOrigins(frontHost);
//    }
    /**
     * 跨越配置
     */
    //@Bean
    /*
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许跨域请求的域名
        config.addAllowedOrigin(frontHost);
        // 是否允许证书 不再默认开启
        config.setAllowCredentials(true);
        // 设置允许的方法 * 为所有
        config.addAllowedMethod("*");
        // 允许任何头
        config.addAllowedHeader("*");
        //config.addExposedHeader("token");
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }*/

//    //
//    @Bean
//    public LocalOverMap getLocalOverMap(){
//        return new LocalOverMap();
//    }
}
