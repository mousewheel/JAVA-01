package org.dromara.hmily.demo.dubbo.order.service;

import org.dromara.hmily.demo.common.order.entity.Order;

/**
 * @Author zhurui
 * @Date 2021/3/16 1:09 下午
 * @Version 1.0
 */
public interface CentralOrderService {

    /**
     * 美元转换为人民币
     *
     * @param order 支付金额
     */
    void usd2Rmb(Order order);

    /**
     * 人民币转换为美元
     *
     * @param order 支付金额
     */
    void rmb2Usd(Order order);
}
