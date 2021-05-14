package io.kimmking.rpcfx.demo.consumer.service;

import io.kimmking.rpcfx.annotation.Service;
import io.kimmking.rpcfx.demo.api.Order;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;

/**
 * @Author zhurui
 * @Date 2021/3/20 5:34 下午
 * @Version 1.0
 */
public class ClientUserServiceImpl {

    @Service(hostUrl = "http://localhost:8080/")
    private UserService userService;
    @Service(hostUrl = "http://localhost:8080/")
    private OrderService orderService;


    public void getUser() {
        
        User user = userService.findById(1);
        System.out.println("find user id=1 from server: " + user.getName());

        Order order = orderService.findOrderById(1992129);
        System.out.println(String.format("find order name=%s, amount=%f", order.getName(), order.getAmount()));
    }

}
