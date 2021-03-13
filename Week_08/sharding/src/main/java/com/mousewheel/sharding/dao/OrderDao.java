package com.mousewheel.sharding.dao;

import com.mousewheel.sharding.common.DateUtils;
import com.mousewheel.sharding.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class OrderDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Long insert(Order order) {
        String insertSql = "insert into t_order ( ORDER_NO, ORDER_DATE, USER_ID, VENDOR_ID, " +
                "ORDER_SUMMARY, ORDER_STATUS, TOTAL_AMT, PAY_STATUS, PAY_CHANNEL, " +
                "PAY_AMT, CREATE_TIME, CREATE_USER) value ";
        String orderValue= String.format("( '%s', '%s', %s, '%s'" +
                            ",'%s','%s',%s,'%s','%s'" +
                            ",%s,'%s','%s')"
                    , order.getOrderNo(), DateUtils.formatDate(order.getOrderDate()), order.getUserId(), order.getVendorId()
                    , order.getOrderSummary(), order.getOrderStatus(), order.getTotalAmt(), order.getPayStatus(), order.getPayChannel()
                    , order.getPayAmt(), DateUtils.formatDate(order.getCreateTime()), order.getCreateUser(), DateUtils.formatDate(order.getUpdateTime()), order.getUpdateUser());


        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(insertSql + orderValue, PreparedStatement.RETURN_GENERATED_KEYS);
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
