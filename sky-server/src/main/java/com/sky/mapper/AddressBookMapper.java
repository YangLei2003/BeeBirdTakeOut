package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    @Insert("INSERT INTO address_book " +
            "(user_id,consignee,sex,phone,province_code,province_name," +
            "city_code,city_name,district_code,district_name,detail,label,is_default) " +
            "VALUES " +
            "(#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName}," +
            "#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void add(AddressBook addressBook);

    @Select("select * from address_book where user_id=#{userId}")
    List<AddressBook> addressList(AddressBook addressBook);

    @Update("update address_book set is_default=1 where id=#{id}")
    void setDefault(AddressBook addressBook);

    @Update("update address_book set is_default=0 where user_id=#{userId}")
    void clareDefault(AddressBook addressBook);

    @Select("SELECT * from address_book where user_id=#{userId} and is_default=1")
    List<AddressBook> getDefault(AddressBook addressBook);

    @Update("UPDATE address_book " +
            "SET consignee = #{consignee}, " +
            "    sex = #{sex}, " +
            "    phone = #{phone}, " +
            "    province_code = #{provinceCode}, " +
            "    province_name = #{provinceName}, " +
            "    city_code = #{cityCode}, " +
            "    city_name = #{cityName}, " +
            "    district_code = #{districtCode}, " +
            "    district_name = #{districtName}, " +
            "    detail = #{detail}, " +
            "    label = #{label}, " +
            "    is_default = #{isDefault} " +
            "WHERE id = #{id}")
    void update(AddressBook addressBook);

    @Select("select * from address_book where id=#{id}")
    AddressBook getById(Long id);

    @Delete("delete from address_book where id=#{id}")
    void deleteById(Long id);
}
