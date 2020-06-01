package cn.peoplevip.other.Controller;


import cn.peoplevip.common.api.AdminService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.Goods;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.redisKey.GoodsKey;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.common.vo.LoginVo;
import cn.peoplevip.common.vo.OrderInfoVo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/9 11:52
 */
@Controller
@ResponseBody
@RequestMapping("/admin")
public class adminController {

    private Logger log = LoggerFactory.getLogger(adminController.class);

    @Autowired
    RedisService redisService;

    @Autowired
    AdminService adminService;

    @ApiOperation(value = "admin登录返回token", notes = "需要username,password,verifyCodetoken")
    @PostMapping("/login")
    public Result<Object> login(@Valid LoginVo loginVo,
                                HttpServletResponse response) {

        //登录,登陆中的错误在server中处理完毕，返回值无用
        String token = adminService.login(response, loginVo);
        if (token != null) {
            HashMap<String, String> data = new HashMap<>(20);
            data.put("token", token);
            return Result.success(data);
        }
        return Result.error(CodeMsg.USER_NOT_ADMIN);
    }

    @ApiOperation(value = "获取管理员用户信息")
    @GetMapping("/info")
    public Result<Object> userInfo(MiaoshaUser user) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        if (user.getRole() != AdminService.roleAdmin) {
            return Result.error(CodeMsg.USER_NOT_ADMIN);
        }
        HashMap<String, Object> result = new HashMap<>(30);
        result.put("roles", new String[]{"admin"});
        user.setPassword(null);
        user.setLoginCount(null);
        user.setSalt(null);
        result.put("user", user);
        List<HashMap<String, Object>> menus = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<>(10);
        //固定数据
        map.put("id",1);
        map.put("parentId",0);
        map.put("createTime",new Date());
        map.put("title","商品");
        map.put("level",0);
        map.put("sort",0);
        map.put("name","pms");
        map.put("icon","product");
        map.put("hidden",0);
        menus.add(map);
        result.put("menus", menus);
        return Result.success(result);

    }

    @ApiOperation(value = "获取订单列表")
    @GetMapping("/order/list")
    public Result<Object> orderListInfo() {
        List<OrderInfoVo> orderInfoVoList = adminService.getOrderList();
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("total", orderInfoVoList.size());
        map.put("list", orderInfoVoList);
        return Result.success(map);
    }

    @ApiOperation(value = "关闭某订单")
    @PostMapping("/order/update/close")
    public Result<CodeMsg> orderClose(@RequestParam("ids") Long ids) {
        int res = adminService.updateCloseOrder(ids);
        if (res > 0) {
            //删除redis内容
            // redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
            //更新成功
            return Result.error(CodeMsg.SUCCESS);
        }
        //未更新
        return Result.error(CodeMsg.DATA_NOT_UPDATE);
    }

    @ApiOperation(value = "添加秒杀商品")
    @PostMapping("/seckill/create/{id}")
    public Result<Object> seckillCreate(@PathVariable("id") String id) {
        long goodsId;
        try {
            goodsId = Long.parseLong(id);
        } catch (Exception e) {
            log.info("添加秒杀商品，商品ID错误");
            return Result.error(CodeMsg.MIAO_SHA_NOFIND);
        }
        boolean isSuccess = adminService.createGoodVo(goodsId);
        if (isSuccess){
            return Result.success(id);
        }
        return Result.success(CodeMsg.DATA_NOT_UPDATE);
    }

    @ApiOperation(value = "修改秒杀商品")
    @PostMapping("/seckill/update/{id}")
    public Result<Object> seckillCreate(@PathVariable("id") String id, GoodsVo goodsVo) {
        long goodsid;
        try {
            goodsid = Long.parseLong(id);
        } catch (Exception e) {
            return Result.error(CodeMsg.MIAO_SHA_NOFIND);
        }
        System.out.println("打印前端输出参数："+goodsVo);
        boolean isSuccess = adminService.updateGoodsVo(goodsVo);
        if (isSuccess){
            redisService.delete(GoodsKey.getGoodsDetail, goodsid + "");
            return Result.success(goodsid);
        }
        return Result.error(CodeMsg.DATA_NOT_UPDATE);
    }

    @ApiOperation(value = "更新商品")
    @PostMapping("/goods/update/{id}")
    public Result<Object> goodsUpdate(@PathVariable("id") String id, Goods goods){
        //插入order/update/close
        //TODO 商品更新
        return Result.success("暂时不实现");
    }

    @ApiOperation(value = "新增商品")
    @PostMapping("/goods/create")
    public Result<Object> goodsCreate(Goods goods){
        //插入
        //TODO 商品更新
        return Result.success("暂时不实现");
    }
}
