package cn.peoplevip.common.api;

import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.domain.OrderInfo;
import cn.peoplevip.common.domain.OrderInfoList;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.common.vo.OrderInfoVo;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/27 11:37
 */
public interface OrderService {

    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userid, long goodsId);

    OrderInfo createOrder(MiaoshaUser user, GoodsVo goods);

    OrderInfo getMiaoshaOrderByOrderId(long orderId);

    void deleteOrders();
    //查看全部订单
    List<OrderInfoVo> getOrderList();


    public int updateCloseOrder(Long ids);

    //获取用户的订单列表
    public List<OrderInfoList> getMiaoshaOrders(MiaoshaUser user);
}
