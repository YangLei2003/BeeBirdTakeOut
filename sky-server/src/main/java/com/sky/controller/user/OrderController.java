package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/user/order/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO= orderService.orderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @GetMapping("/user/order/historyOrders")
    //JSON才用@RequestBody，请求参数等纯参数用@RequestParam
    //@RequestParam只能接收单个参数，所以要多个@RequestParam来分别接收参数
    //required=false的意思是非必须参数
    public Result<PageResult> historyOrders(@RequestParam int page, @RequestParam int pageSize, @RequestParam(required = false) Integer status){
        PageResult pageResult=orderService.historyOrders(page,pageSize,status);
        return Result.success(pageResult);
    }

    @GetMapping("/user/order/reminder/{id}")
    public Result reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }

}
