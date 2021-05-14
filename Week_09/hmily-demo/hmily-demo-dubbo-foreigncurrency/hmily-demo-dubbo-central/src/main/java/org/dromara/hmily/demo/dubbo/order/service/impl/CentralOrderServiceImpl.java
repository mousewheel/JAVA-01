package org.dromara.hmily.demo.dubbo.order.service.impl;

import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.demo.common.account.api.CurrencyAccountService;
import org.dromara.hmily.demo.common.account.dto.AccountDTO;
import org.dromara.hmily.demo.common.order.entity.Order;
import org.dromara.hmily.demo.dubbo.order.service.CentralOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author zhurui
 * @Date 2021/3/16 1:11 下午
 * @Version 1.0
 */
@Service("centralOrderService")
public class CentralOrderServiceImpl implements CentralOrderService {

    private final CurrencyAccountService rmbAccountService;

    private final CurrencyAccountService usdAccountService;


    @Autowired(required = false)
    public CentralOrderServiceImpl(CurrencyAccountService rmbAccountService,
                                   CurrencyAccountService usdAccountService) {
        this.rmbAccountService = rmbAccountService;
        this.usdAccountService = usdAccountService;
    }


    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public void usd2Rmb(Order order) {
        //创建交易明细->status new
        //添加中间表 做处理
        //扣款美元 -> status deduct
        usdAccountService.decrease(this.buildAccountDTO(order));
        //增加rmb -> status success
        rmbAccountService.increase(this.buildAccountDTO(order));
    }

    @Override
    public void rmb2Usd(Order order) {

    }

    private AccountDTO buildAccountDTO(Order order) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        return accountDTO;
    }

    public void confirm(Order order) {
    }

    public void cancel(Order order) {
    }

}
