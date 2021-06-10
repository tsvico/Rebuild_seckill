-- 秒杀执行存储过程
-- ;分号决定是否换行
DELIMITER $$ -- 新的换行 由 ; 转换为 $$
-- 定义存储过程 in 输入参数 out 输出参数
-- row_count() 返回上一条修改类型sql (delete, insert, update)的影响行数的影响函数
-- row_count() 0表示未修改数据 >0 表示修改的行数 <0表示sql错误/未执行
create procedure `miaosha`.`execute_miaosha`(in v_userId bigint, in v_goodsId bigint,
                                             in v_orderId bigint, out r_result int)
begin
    declare insert_count int default 0;
    start transaction ;
    -- 开始事务
    -- 插入订单
    insert ignore into miaosha_order
        (user_id, goods_id, order_id)
    values (v_userId, v_goodsId, v_orderId);
    select row_count() into insert_count;
    if (insert_count = 0) then -- 未修改
        rollback;
        set r_result = -1; -- 返回重复秒杀
    elseif (insert_count < 0) then -- 没有执行过或者错误
        rollback;
        set r_result = -2; -- 返回系统异常
    else
        update miaosha_goods m,goods g
        set m.stock_count = m.stock_count - 1,
            g.goods_stock = g.goods_stock - 1
        where m.goods_id = g.id
          and m.goods_id = v_goodsId
          and m.stock_count > 0; -- 同时更新两个库存
        select ROW_COUNT() INTO insert_count;
        if (insert_count = 0) then
            rollback;
            SET r_result = 0;
        elseif (insert_count < 0) then
            rollback;
            set r_result = -2;
        else
            commit; -- 提交
            set r_result = 1; -- 返回成功
        end if;
    end if;
end $$
-- 存储过程定义结束