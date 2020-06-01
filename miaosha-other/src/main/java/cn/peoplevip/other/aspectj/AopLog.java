package cn.peoplevip.other.aspectj;

import com.alibaba.fastjson.JSON;
import eu.bitwalker.useragentutils.UserAgent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/5 0:44
 * 使用 aop 切面记录请求日志信息
 */
@Aspect
@Component
//@Slf4j
public class AopLog {
    private final Logger log = LoggerFactory.getLogger(AopLog.class);
    private static final String START_TIME = "request-start";

    /**
     * 存储拦截的请求，在同一个线程里,放在最后一块打印
     */
    private static final ThreadLocal<HashMap<String, String>> messageHolder = new ThreadLocal<>();

    /**
     * 切入点
     */
    @Pointcut("execution(* cn.peoplevip.*.Controller.*.*(..))")
    public void log() {

    }


    //拦截自定义注解
    /*
    @Around("log()")
    public Object authorityHandler(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        //判断方法上是否有此注解
        if (method.isAnnotationPresent(WorkSheetAnnotation.class)) {}
        //判断类上是否有次注解
        if(pjp.getTarget().getClass().isAnnotationPresent(WorkSheetAnnotation.class)){}
        //获取方法上的注解值
        method.getAnnotation(NoAuth.class)
        log.info("method{}",method); System.out.println("!!!!!!!!!!!!!!!!!已被拦截");
        return pjp.proceed();
    }*/


    /**
     * 前置操作
     *
     * @param point 切入点
     */
    @Before("log()")
    public void beforeLog(JoinPoint point) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        HashMap<String, String> result = new HashMap<>(16);
        result.put("[请求URL]", request.getRequestURI());
        String ip;
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }
        result.put("[请求IP]", ip);
        result.put("[请求类名.方法]",
                point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
        result.put("[请求参数]", JSON.toJSONString(request.getParameterMap()));
        messageHolder.set(result);
        Long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        HashMap<String, String> map = messageHolder.get();
        map.put("【返回值】", JSON.toJSONString(result));
        messageHolder.set(map);
        return result;
    }

    /**
     * 后置操作
     */
    @AfterReturning("log()")
    public void afterReturning() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();


        String header = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        HashMap<String, String> map = messageHolder.get();
        messageHolder.remove();
        log.info("【请求】: {},【请求耗时】：{}毫秒,【浏览器类型】：{}，【操作系统】：{}", JSON.toJSONString(map), end - start, userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString());
    }
}
