package cn.peoplevip.other.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/6 9:39
 * 验证码配置
 */
@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        // 字体
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "宋体,楷体,微软雅黑");
        //图片边框
        properties.put(Constants.KAPTCHA_BORDER, "no");
        properties.put("kaptcha.obscurificatortextproducer.font.color", "black");
        properties.put("kaptcha.textproducer.char.space", "6");
        //验证码长度
        properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        //宽高
        properties.put(Constants.KAPTCHA_IMAGE_WIDTH,"120");
        properties.put("kaptcha.image.height", "35");
        properties.put("kaptcha.textproducer.font.size", "25");

        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
