package cn.peoplevip.other.dao;

import cn.peoplevip.common.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/26 9:18
 */
@Mapper
public interface MiaoshaUserDao {
    //@Select("select * from miaosha_user where id = #{id}")
    @Select("select u.*,r.role from miaosha_user as u right join roles r on (u.roles_id = r.id) where u.id = #{id}")
    MiaoshaUser getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(MiaoshaUser toBeUpdate);

    @Update("UPDATE `miaosha_user` SET `login_count` = 1+`login_count` WHERE `id` = #{id}")
    void updateLoginCount(@Param("id") long id);
}
