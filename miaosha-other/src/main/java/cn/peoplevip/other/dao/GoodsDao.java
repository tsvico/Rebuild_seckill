package cn.peoplevip.other.dao;


import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.domain.MiaoshaGoods;
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
public interface GoodsDao {

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

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

    //数据库sql同时更新两个表
    @Update("update miaosha_goods m,goods g set m.stock_count = m.stock_count - 1,g.goods_stock = g.goods_stock -1 where m.goods_id = g.id and m.goods_id = #{goodsId} and m.stock_count > 0")
    public int reduceStock(MiaoshaGoods g);

    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    public int resetStock(MiaoshaGoods g);

    @Select("select g.* from goods g LIMIT 0, 1000")
    List<Goods> listAllGoods();

    @Select("select * from goods where id = #{id}")
    Goods getGoodsByGoodsId(long id);

    @Insert("INSERT ignore INTO `miaosha_goods`(`goods_id`) VALUES (#{goodsid})")
    int createGoodVo(long goodsid);

    @Update("UPDATE `miaosha_goods` SET `miaosha_price` = #{miaoshaPrice}, `stock_count` = #{stockCount}, `start_date` = #{startDate}, `end_date` = #{endDate} WHERE `goods_id` = #{id}")
    int updateGoodVo(GoodsVo g);

    @Select(value =
            "call execute_miaosha(#{userId,mode=IN,jdbcType=BIGINT}, #{goodsId,mode=IN,jdbcType=BIGINT}, #{orderId,mode=IN,jdbcType=BIGINT}, #{result,mode=OUT,jdbcType=INTEGER}) "
    )
    @Options(statementType = StatementType.CALLABLE)  //执行存储过程
    void executeMiaosha(Map map);
}
