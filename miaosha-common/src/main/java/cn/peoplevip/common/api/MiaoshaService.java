package cn.peoplevip.common.api;

import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.vo.GoodsVo;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 20:47
 * 功能
 */
public interface MiaoshaService {
    public int executeMiaosha(long userId, GoodsVo goods);
    //public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods);
    public long getMiaoshaResult(Long userid, long goodsId);
    public void reset(List<GoodsVo> goodsList);
    public void setGoodsOver(Long id);
    public boolean checkPath(MiaoshaUser user, long goodsId, String path);
    public String createMiaoshaPath(MiaoshaUser user, long goodsId);
}
