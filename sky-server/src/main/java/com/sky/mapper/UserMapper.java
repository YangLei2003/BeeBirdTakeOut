package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time)" +
            " values " +
            "(#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    //主键回显，这个是必须的，可以在debug里看出来，效果为：插入成功后，id会回填到user对象中
    //如果没有回显的话，即使开了主键自增，也无法插入回user对象的id属性中
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(User user);
}
