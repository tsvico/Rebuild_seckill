package cn.peoplevip.other.Controller;

import cn.peoplevip.common.api.GoodsService;
import cn.peoplevip.common.api.MiaoshaUserService;
import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import cn.peoplevip.common.vo.GoodsVo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/goods")
public class GoodsController {

    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService userService;

    //@Reference
    //RedisService redisService;

    @Autowired
    GoodsService goodsService;


    @ApiOperation(value = "返回商品列表页，非API")
    @GetMapping("/to_list")
    public String goods(Model model, MiaoshaUser user) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @ApiOperation(value = "商品列表页接口")
    @GetMapping("/list")
    @ResponseBody
    public Result<Map<String,Object>> goodsList(MiaoshaUser user) {
        HashMap<String, Object> dat = new HashMap<>();
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        user.setSalt(null);
        user.setPassword(null);
        user.setRegisterDate(null);
        dat.put("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return Result.error(CodeMsg.MIAO_SHA_NOFIND);
        }
        dat.put("goodsList", goodsList);
        return Result.success(dat);
    }

    @ApiOperation(value = "商品列表页接口:全部商品（包括非秒杀商品）")
    @GetMapping("/alllist")
    @ResponseBody
    public Result<Map> goodsAllList(MiaoshaUser user) {
        log.info("未登录");
        HashMap<String, Object> dat = new HashMap<>();
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        user.setSalt(null);
        user.setPassword(null);
        user.setRegisterDate(null);
        dat.put("user", user);
        //查询商品列表
        List<Goods> goodsList = goodsService.listAllGoods();
        if (goodsList == null) {
            return Result.error(CodeMsg.MIAO_SHA_NOFIND);
        }
        dat.put("goodsList", goodsList);
        return Result.success(dat);
    }
}
