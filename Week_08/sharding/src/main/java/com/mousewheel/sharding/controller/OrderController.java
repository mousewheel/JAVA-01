package com.mousewheel.sharding.controller;

import com.mousewheel.sharding.entity.Order;
import com.mousewheel.sharding.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;


@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/createOrder")
    public String createOrder() {
        Order order = new Order();
        order.setOrderNo("no-" + System.currentTimeMillis());
        order.setUserId(1L);
        order.setVendorId(1L);
        order.setOrderSummary("summary test");
        order.setOrderDate(new Date());
        order.setPayStatus("1");
        order.setPayChannel("ali");
        order.setOrderStatus("1");
        order.setPayAmt(new BigDecimal(100));
        order.setTotalAmt(new BigDecimal(100));
        order.setCreateTime(new Date());
        order.setCreateUser("test");
        orderService.insert(order);
        try {
            Thread.sleep(1);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        order.setOrderNo("no-" + System.currentTimeMillis());
        order.setUserId(2L);
        orderService.insert(order);

        return "success";
    }
}
