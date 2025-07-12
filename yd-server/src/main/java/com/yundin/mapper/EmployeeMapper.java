package com.yundin.mapper;

import com.yundin.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    //在employee标中查找username=动态username的记录
    Employee getByUsername(String username);
/**
 * 插入员工数据
 * 这个持久层是把业务层的数据插入数据库。
 * 这个员工数据只插入到员工表这一张表中，所以用insert注解
 */
@Insert("insert into employee (username,name,password,phone,sex,id_number,status,create_time,update_time,create_user,update_user) " +
        "values (#{username},#{name},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
void insert(Employee employee);
}
