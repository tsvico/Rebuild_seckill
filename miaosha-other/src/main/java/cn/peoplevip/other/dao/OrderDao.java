package cn.peoplevip.other.dao;

import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.domain.OrderInfo;
import cn.peoplevip.common.domain.OrderInfoList;
import cn.peoplevip.common.vo.OrderInfoVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/28 17:37
 * 功能
 */
@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userid} and goods_id=#{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userid") Long userid, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(id,user_id,goods_id,goods_name,goods_count,goods_price,order_channel,`status`,create_date) values " +
            "(#{id},#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    //@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    public long insert(OrderInfo orderInfo);

    @Insert("insert ignore into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public long insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("SELECT * FROM `order_info` where id=#{orderId}")
    OrderInfo getOrderByOrderId(long orderId);

    @Delete("delete from order_info")
    public void deleteOrders();

    @Delete("delete from miaosha_order")
    public void deleteMiaoshaOrders();

    /**
     * 后台查询订单
     */
    @Select("select m.id,u.nickname as 'username',a.id as 'orderId',a.create_date,a.goods_price,a.`status`,a.order_channel from order_info as a left join miaosha_order as m on m.order_id=a.id left join miaosha_user u on u.id=a.user_id")
    List<OrderInfoVo> getAllOrderInfo();

    @Update("UPDATE `order_info` SET `status` = 4 WHERE `id` = #{ids}")
    int updateCloseOrder(Long ids);

    //更好的性能resultMap
    @Results(value = {
            @Result(column="id", property="id", id=true),
            @Result(column="user_id", property="userId"),
            @Result(column="goods_id ", property="goodsId"),
            @Result(column="delivery_addr_id", property="deliveryAddrId"),
            @Result(column="goods_name", property="goodsName"),
            @Result(column="goods_count", property="goodsCount"),
            @Result(column="goods_price", property="goodsPrice"),
            @Result(column="order_channel", property="orderChannel"),
            @Result(column="status", property="status"),
            @Result(column="create_date", property="createDate"),
            @Result(column="pay_date", property="payDate"),
            @Result(column="goodImg", property="goodImg")
    })
    @Select("select o.*,g.goods_img as goodImg from order_info as o left join goods as g on g.id = o.goods_id where o.user_id = #{id}")
    List<OrderInfoList> getUserOrderInfos(MiaoshaUser user);
}
