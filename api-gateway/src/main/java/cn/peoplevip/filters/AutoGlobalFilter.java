package cn.peoplevip.filters;

import cn.peoplevip.utils.AESSecretUtil;
import cn.peoplevip.utils.FilterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @date 2020/4/16 11:51
 * @Description 自定义全局过滤器，进行统一鉴权
 * 要求：必须实现GloblFailter,Ordered 并且实现里面的两个方法
 */
@Component
@EnableConfigurationProperties({FilterProperties.class})
public class AutoGlobalFilter implements GlobalFilter, Ordered {

    @Value("${miaosha.aespass}")
    String aesPass;

    @Autowired
    private FilterProperties filterProperties;

    //过滤器逻辑
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isAllowPath(exchange.getRequest().getURI().getPath())){
            //不过滤
            return chain.filter(exchange);
        }
        //从请求头中取出token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        //未携带token或token在黑名单内
        if (token == null || token.isEmpty()) {
            ServerHttpResponse originalResponse = exchange.getResponse();
            originalResponse.setStatusCode(HttpStatus.OK);
            originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] response = "{\"code\": \"401\",\"msg\": \"401 Unauthorized.\"}"
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
            return originalResponse.writeWith(Flux.just(buffer));
        }
        String AuthorizationToken;
        //取出token包含的身份，用于业务处理
        try{
            //url中base64的+会被转码，请求头不受影响
            AuthorizationToken = AESSecretUtil.decryptBase64ToStr(token, aesPass);
        }catch (Exception e){
            ServerHttpResponse originalResponse = exchange.getResponse();
            originalResponse.setStatusCode(HttpStatus.OK);
            originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] response = "{\"code\": \"10002\",\"msg\": \"用户验证失败.\"}"
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
            return originalResponse.writeWith(Flux.just(buffer));
        }

        if (AuthorizationToken == null || AuthorizationToken.isEmpty()) {
            ServerHttpResponse originalResponse = exchange.getResponse();
            originalResponse.setStatusCode(HttpStatus.OK);
            originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] response = "{\"code\": \"10002\",\"msg\": \"invalid token.\"}"
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
            return originalResponse.writeWith(Flux.just(buffer));
        }
        //将现在的request，添加当前身份
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("Authorization", AuthorizationToken).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        ////第二种方法 未验证
//        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
//            httpHeader.set("aaaa", "bbb");
//            httpHeader.set("xxx", "cc");
//            httpHeader.set("bbxx", "bbbx");
//            httpHeader.set("aaaa", "bbb");
//        };
//        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
//        exchange.mutate().request(serverHttpRequest).build();
//第三种方法 未验证
//        // 定义新的消息头
//        HttpHeaders headers = new HttpHeaders();
//        headers.putAll(exchange.getRequest().getHeaders());
//        // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
//        int length = bytes.length;
//        headers.remove(HttpHeaders.CONTENT_LENGTH);
//        headers.setContentLength(length);
//        headers.set(HttpHeaders.CONTENT_TYPE, "text/xml");
//        newRequest = new ServerHttpRequestDecorator(newRequest) {
//            @Override
//            public HttpHeaders getHeaders() {
//                return headers;
//            }
//        };

        return chain.filter(mutableExchange);
    }

    private boolean isAllowPath(String path) {
        //遍历白名单
        for (String allowPath : filterProperties.getAllowPaths()) {
            //判断是否允许
            if(path.startsWith(allowPath)){
                return true;
            }
        }
        return  false;
    }

    //标识当前过滤器的优先级,返回值越小,优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
