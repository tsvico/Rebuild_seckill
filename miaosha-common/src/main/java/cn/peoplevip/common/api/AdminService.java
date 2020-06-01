package cn.peoplevip.common.api;

import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.common.vo.LoginVo;
import cn.peoplevip.common.vo.OrderInfoVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 22:28
 * 功能
 */
public interface AdminService {
    //普通用户
    public static Integer roleOrdinary = 0;
    /**
     * 管理员
     */
    public static Integer roleAdmin = 1;
    public String login(HttpServletResponse response, LoginVo loginVo);
    public List<OrderInfoVo> getOrderList();
    public int updateCloseOrder(Long ids);
    public Goods getGoodsById(long goodsid);
    public boolean createGoodVo(long goodsid);
    public boolean updateGoodsVo(GoodsVo goodsVo);
}
