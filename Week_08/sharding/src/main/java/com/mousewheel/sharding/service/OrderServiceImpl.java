package com.mousewheel.sharding.service;

import com.mousewheel.sharding.dao.OrderDao;
import com.mousewheel.sharding.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Override
    public Long insert(Order order) {
        return orderDao.insert(order);
    }
}
