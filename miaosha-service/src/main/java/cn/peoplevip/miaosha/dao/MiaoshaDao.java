package cn.peoplevip.miaosha.dao;


import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.domain.OrderInfo;
import cn.peoplevip.common.vo.GoodsVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 16:46
 * 功能
 */
@Mapper
public interface MiaoshaDao {
    /**
     *  执行存储过程
     */
    @Select(value =
            "call execute_miaosha(#{userId,mode=IN,jdbcType=BIGINT}, #{goodsId,mode=IN,jdbcType=BIGINT}, #{orderId,mode=IN,jdbcType=BIGINT}, #{result,mode=OUT,jdbcType=INTEGER}) "
    )
    @Options(statementType = StatementType.CALLABLE)
    void executeMiaosha(Map<String,Object> map);

    @Select("select * from miaosha_order where user_id=#{userid} and goods_id=#{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userid") Long userid, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(id,user_id,goods_id,goods_name,goods_count,goods_price,order_channel,`status`,create_date) values " +
            "(#{id},#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    public long OrderInfoInsert(OrderInfo orderInfo);

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

    @Results(value = {
            @Result(column="id", property="id", id=true),
            @Result(column="goods_name", property="goodsName"),
            @Result(column="goods_title ", property="goodsTitle"),
            @Result(column="goods_img", property="goodsImg"),
            @Result(column="goods_detail", property="goodsDetail"),
            @Result(column="goods_price", property="goodsPrice"),
            @Result(column="goods_stock", property="goodsStock"),
            @Result(column="miaosha_price", property="miaoshaPrice"),
            @Result(column="stock_count", property="stockCount"),
            @Result(column="start_date", property="startDate", jdbcType=JdbcType.TIMESTAMP),
            @Result(column="end_date", property="endDate", jdbcType=JdbcType.TIMESTAMP)
    })
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

}
