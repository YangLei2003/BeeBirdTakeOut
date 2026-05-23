package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    @Transactional
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //1.查询地址是不是为空
        AddressBook addressId = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressId==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //2.查询购物车是否为空
        Long userId= BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByID(userId);
        if(shoppingCarts==null || shoppingCarts.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        Orders orders;
        //builder是lombok的静态方法，注意用法
        //直接orders.builder()会报错,正确用法应该是orders=Orders.builder()
        //并且builder方法会创建一个新的对象，而不是修改原来的对象(如果不orders=的话)
        orders=Orders.builder()
                .orderTime(LocalDateTime.now())
                .userId(userId)
                .status(Orders.PENDING_PAYMENT)
                .payStatus(Orders.UN_PAID)
                .number(String.valueOf(System.currentTimeMillis()))//根据时间生成订单号
                .phone(addressId.getPhone())
                .consignee(addressId.getConsignee())
                .build();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        //向总订单表插入一条数据
        orderMapper.insert(orders);
        List<OrderDetail> orderDetails = new ArrayList<>();
        //向订单细节表插入n条数据
        for (int i = 0; i < shoppingCarts.size(); i++) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCarts.get(i),orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderMapper.saveDetail(orderDetails);
        //清空购物车
        shoppingCartMapper.clear(userId);
        //封装并返回数据
        OrderSubmitVO orderSubmitVO;
        orderSubmitVO = orderMapper.orderReturn(orders.getId());
        return orderSubmitVO;
    }

    public void reminder(Long id) {
        Orders orders=orderMapper.getById(id);
        if(orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map map=new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号:"+ orders.getNumber());

        //通过webSocket向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    public PageResult historyOrders(int page,int pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);

        OrdersPageQueryDTO queryDTO=new OrdersPageQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setPageSize(pageSize);
        queryDTO.setStatus(status);
        Long userId = BaseContext.getCurrentId();
        queryDTO.setUserId(userId);

        Page<HistoryOrders> pageResult=orderMapper.pageQuery(queryDTO);

        long total = pageResult.getTotal();
        List<HistoryOrders> records=pageResult.getResult();
        return new PageResult(total, records);
    }
}
