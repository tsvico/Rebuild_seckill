package cn.peoplevip.common.api;


import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.vo.LoginVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/26 9:26
 */
public interface MiaoshaUserService {
    MiaoshaUser getById(long id);
    boolean updatePassword(String token, long id, String passwordNew);
    public Map<String, Object> login(HttpServletResponse response, LoginVo loginVo);
    public String testLogin(String username, String password);
    void updateLoginCount(long parseLong);
    MiaoshaUser getByToken(String token, HttpServletResponse response);
    public String getLoginToken(HttpServletRequest request);
    public String getLoginToken(String ParamToken,String headerToken);
}
