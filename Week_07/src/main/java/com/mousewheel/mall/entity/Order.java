package com.mousewheel.mall.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {

private Long id;

private String orderNo;

private Date orderDate;

private Long userId;

private Long vendorId;

private String orderSummary;

private String orderStatus;

private BigDecimal totalAmt;

private String payStatus;

private String payChannel;

private BigDecimal payAmt;

private String createUser;

private Date createTime;

private String updateUser;

private Date updateTime;

}
