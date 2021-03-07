package com.mousewheel.mall.service;

import com.mousewheel.mall.entity.Order;
import java.util.List;

public interface OrderService {

    Boolean batchInsert(List<Order> orderList);
}
