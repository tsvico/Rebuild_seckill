package cn.peoplevip.other.Service;

import cn.peoplevip.common.api.AdminService;
import cn.peoplevip.common.api.GoodsService;
import cn.peoplevip.common.api.MiaoshaUserService;
import cn.peoplevip.common.api.OrderService;
import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.common.vo.LoginVo;
import cn.peoplevip.common.vo.OrderInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/9 18:12
 * 功能
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;


    @Override
    public String login(HttpServletResponse response, LoginVo loginVo) {
        Map<String, Object> map = userService.login(response, loginVo);
        MiaoshaUser user = (MiaoshaUser) map.get("user");
        //管理员的权限role为1
        if (user != null && user.getRole() == roleAdmin) {
            return (String) map.get("token");
        }
        return null;
    }
    @Override
    public List<OrderInfoVo> getOrderList(){
        return orderService.getOrderList();
    }


    @Override
    public int updateCloseOrder(Long ids) {
        return orderService.updateCloseOrder(ids);
    }

    /**
     * 暂时不用
     * @param goodsid
     * @return
     */
    @Override
    public Goods getGoodsById(long goodsid) {
        return goodsService.getGoodsByGoodsId(goodsid);
    }

    @Override
    public boolean createGoodVo(long goodsid) {
        return goodsService.createGoodVo(goodsid);
    }

    @Override
    public boolean updateGoodsVo(GoodsVo goodsVo) {
        double miaoshaPrice = goodsVo.getMiaoshaPrice();
        int stockCount = goodsVo.getStockCount();
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        GoodsVo g = new GoodsVo();
        g.setId(goodsVo.getId());
        g.setMiaoshaPrice(miaoshaPrice);
        g.setStockCount(stockCount);
        g.setStartDate(startDate);
        g.setEndDate(endDate);
        return goodsService.updateGoodVo(g);
    }
}
