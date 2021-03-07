package com.mousewheel.mall.dao;

import com.mousewheel.mall.common.DateUtils;
import com.mousewheel.mall.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Boolean batchInsert(List<Order> orderList) {
        Long startTime = System.currentTimeMillis();
        String insertSql = "insert into mall.m_order ( ID, ORDER_NO, ORDER_DATE, USER_ID, VENDOR_ID, " +
                "ORDER_SUMMARY, ORDER_STATUS, TOTAL_AMT, PAY_STATUS, PAY_CHANNEL, " +
                "PAY_AMT, CREATE_TIME, CREATE_USER) values  ";
        StringBuilder sbOrderValues = new StringBuilder();
        String orderValue;
        for (Order order : orderList) {
            orderValue = String.format("('%s', '%s', '%s', '%s', '%s'" +
                            ",'%s','%s',%s,'%s','%s'" +
                            ",%s,'%s','%s'),"
                    , order.getId(), order.getOrderNo(), DateUtils.formatDate(order.getOrderDate()), order.getUserId(), order.getVendorId()
                    , order.getOrderSummary(), order.getOrderStatus(), order.getTotalAmt(), order.getPayStatus(), order.getPayChannel()
                    , order.getPayAmt(), DateUtils.formatDate(order.getCreateTime()), order.getCreateUser(), DateUtils.formatDate(order.getUpdateTime()), order.getUpdateUser());
            sbOrderValues.append(orderValue);
        }
        Long endTime = System.currentTimeMillis();
        System.out.println("Concat values time cost:" + (endTime-startTime) + " ms");
        startTime = endTime;
        int effectNum = jdbcTemplate.update(insertSql + sbOrderValues.substring(0, sbOrderValues.length() - 1));
        endTime = System.currentTimeMillis();
        System.out.println("Batch insert time cost:"+(endTime-startTime) + " ms");

        if (effectNum > 0) {
            return true;
        } else {
            return false;
        }

    }
}
