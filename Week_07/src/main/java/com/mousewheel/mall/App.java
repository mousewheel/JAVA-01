package com.mousewheel.mall;

import com.mousewheel.mall.entity.Order;
import com.mousewheel.mall.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        ConfigurableApplicationContext context = app.run(args);

        List<Order> orderList = new ArrayList<>();
        Order order;
        for (int i = 1; i <= 1000000; i++) {
            order = new Order();
            order.setId(Long.valueOf(i));
            order.setOrderNo("no" + order.getId());
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
            orderList.add(order);
        }
        OrderService orderService = (OrderService) context.getBean("orderService");
        orderService.batchInsert(orderList);

        context.close();
    }


}
