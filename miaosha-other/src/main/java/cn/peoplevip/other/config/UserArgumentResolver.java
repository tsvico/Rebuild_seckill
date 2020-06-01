package cn.peoplevip.other.config;

import cn.peoplevip.common.Utils.UserContext;
import cn.peoplevip.common.api.MiaoshaUserService;
import cn.peoplevip.common.domain.MiaoshaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/27 8:55
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    MiaoshaUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    /**
     * 提前拦截请求，并把token转化为类对象
     *
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        // System.out.println("UserArgumentResolver-----getUser");
        MiaoshaUser user = UserContext.getUser();

        return user;
        /*
        //获取user在拦截器里执行，这里不用了
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

        String ParamToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(ParamToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(ParamToken) ? cookieToken : ParamToken;
        //取出cookie，依赖token来获取登录状态
        MiaoshaUser user = userService.getByToken(token, response);
        return user;
         */
    }
}
