package com.mousewheel.mall.service;

import com.google.common.collect.Lists;
import com.mousewheel.mall.dao.OrderDao;
import com.mousewheel.mall.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Override
    public Boolean batchInsert(List<Order> orderList) {
        Long startTime = System.currentTimeMillis();
        // 拆分为每10W一批插入
        List<List<Order>> orderListPartion = Lists.partition(orderList, 100000);
        Long endTime = System.currentTimeMillis();
        System.out.println("Split Time cost: " + (endTime - startTime) + " ms");

        // 并行插入
        orderListPartion.parallelStream().forEach(sublist -> orderDao.batchInsert(sublist));
        endTime = System.currentTimeMillis();

        System.out.println("Total Time cost: " + (endTime - startTime) + " ms");

        return true;
    }
}
