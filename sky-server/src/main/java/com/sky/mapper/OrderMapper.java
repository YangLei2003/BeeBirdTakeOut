package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.HistoryOrders;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {


    void insert(Orders orders);

    void saveDetail(List<OrderDetail> orderDetails);

    @Select("select id,amount as orderAmount,order_time,number as orderNumber from orders where id=#{id}")
    OrderSubmitVO orderReturn(Long id);

    @Select("select * from order where status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);


    @Update("update orders set status=#{status} and cancel_reason=#{cancelReason} and cancel_time=#{cancelTime} where id=#{id} ")
    void update(Orders orders);


    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    @Select("select * from orders where user_id=#{userId} and status=#{status}")
    Page<HistoryOrders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
}
