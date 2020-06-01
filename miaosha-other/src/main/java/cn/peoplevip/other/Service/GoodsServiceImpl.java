package cn.peoplevip.other.Service;

import cn.peoplevip.common.api.GoodsService;
import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.domain.MiaoshaGoods;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.other.dao.GoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 21:10
 * 功能
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired(required = false)
    GoodsDao goodsDao;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    @Override
    public Goods getGoodsByGoodsId(long id) {
        return goodsDao.getGoodsByGoodsId(id);
    }

    @Override
    public List<Goods> listAllGoods() {
        return goodsDao.listAllGoods();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }


    @Override
    public void resetStock(List<GoodsVo> goodsList) {
        for (GoodsVo goods : goodsList) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }

    @Override
    public boolean createGoodVo(long goodsid) {
        return goodsDao.createGoodVo(goodsid) > 0;
    }

    @Override
    public boolean updateGoodVo(GoodsVo g) {
        return goodsDao.updateGoodVo(g) > 0;
    }

}
