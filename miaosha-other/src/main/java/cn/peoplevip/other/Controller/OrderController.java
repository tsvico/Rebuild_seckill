package cn.peoplevip.other.Controller;

import cn.peoplevip.common.api.AdminService;
import cn.peoplevip.common.api.GoodsService;
import cn.peoplevip.common.api.MiaoshaUserService;
import cn.peoplevip.common.api.OrderService;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.domain.OrderInfo;
import cn.peoplevip.common.domain.OrderInfoList;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import cn.peoplevip.common.vo.GoodsVo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 11:07
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    MiaoshaUserService userService;

    //@Reference
    //RedisService redisService;
    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    /**
     * 获取所有订单详情
     * @param user 登录用户
     * @return json格式数据
     */
    @ApiOperation(value = "获取所有订单(查看购物车)")
    @GetMapping("/orders")
    @ResponseBody
    public Result<List<OrderInfoList>> getOrder(MiaoshaUser user) {

        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        List<OrderInfoList> orderInfos = orderService.getMiaoshaOrders(user);
        if (orderInfos == null) {
            return Result.error(CodeMsg.ORDER_DATAILNOFOUND);
        }
        return Result.success(orderInfos);
    }

    /**
     * 订单详情
     *
     * @param user 登录用户
     * @param orderId 订单ID
     * @return json格式数据
     */
    @ApiOperation(value = "订单详情")
    @GetMapping("/orderDetail/{orderId}")
    @ResponseBody
    public Result<Map<String,Object>> orderDetail(MiaoshaUser user,
                                                  @PathVariable("orderId") long orderId) {

        HashMap<String, Object> map = new HashMap<>();
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        OrderInfo orderInfo = orderService.getMiaoshaOrderByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_DATAILNOFOUND);
        }
        //对访问订单的用户鉴权
        if (!orderInfo.getUserId().equals(user.getId())) {
            if (user.getRole() != AdminService.roleAdmin) {
                return Result.error(CodeMsg.ORDER_DATAILNOFOUND);
            }
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(orderInfo.getGoodsId());
        map.put("orderInfo", orderInfo);
        map.put("goods", goods);
        return Result.success(map);
    }

}
