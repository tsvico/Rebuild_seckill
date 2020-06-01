package cn.peoplevip.common.api;

import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.vo.GoodsVo;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/27 11:37
 */

public interface GoodsService {

    List<GoodsVo> listGoodsVo();
    Goods getGoodsByGoodsId(long id);
    List<Goods> listAllGoods();
    GoodsVo getGoodsVoByGoodsId(long goodsId);
    void resetStock(List<GoodsVo> goodsList);
    boolean createGoodVo(long goodsid);
    boolean updateGoodVo(GoodsVo g);
}
