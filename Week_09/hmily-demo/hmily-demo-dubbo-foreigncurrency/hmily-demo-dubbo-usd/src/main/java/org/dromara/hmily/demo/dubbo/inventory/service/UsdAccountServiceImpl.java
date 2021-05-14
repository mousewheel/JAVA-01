package org.dromara.hmily.demo.dubbo.inventory.service;

import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.demo.common.account.api.CurrencyAccountService;
import org.dromara.hmily.demo.common.account.dto.AccountDTO;
import org.dromara.hmily.demo.common.account.mapper.CurrencyAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author zhurui
 * @Date 2021/3/16 12:51 下午
 * @Version 1.0
 */
@Service("usdAccountService")
public class UsdAccountServiceImpl implements CurrencyAccountService {
    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UsdAccountServiceImpl.class);


    private final CurrencyAccountMapper currencyAccountMapper;

    @Autowired(required = false)
    public UsdAccountServiceImpl(final CurrencyAccountMapper currencyAccountMapper) {
        this.currencyAccountMapper = currencyAccountMapper;
    }

    @Override
    @HmilyTCC(confirmMethod = "confirmIncrease", cancelMethod = "cancelIncrease")
    public boolean increase(AccountDTO accountDTO) {
        return false;
    }

    @Override
    @HmilyTCC(confirmMethod = "confirmDecrease", cancelMethod = "cancelDecrease")
    public boolean decrease(AccountDTO accountDTO) {
        return currencyAccountMapper.decrease(accountDTO) > 0;
    }


    /**
     * Confirm boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDecrease(AccountDTO accountDTO) {
        LOGGER.info("============dubbo tcc 执行确认美元付款接口===============");
        currencyAccountMapper.confirmDecrease(accountDTO);
        return Boolean.TRUE;
    }

    /**
     * Cancel boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelDecrease(AccountDTO accountDTO) {
        LOGGER.info("============ dubbo tcc 执行取消美元付款接口===============");
        currencyAccountMapper.cancelDecrease(accountDTO);
        return Boolean.TRUE;
    }



    /**
     * Confirm boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmIncrease(AccountDTO accountDTO) {
        LOGGER.info("============dubbo tcc 执行确认美元汇款接口===============");
        currencyAccountMapper.confirmIncrease(accountDTO);
        return Boolean.TRUE;
    }

    /**
     * Cancel boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelIncrease(AccountDTO accountDTO) {
        LOGGER.info("============ dubbo tcc 执行取消美元汇款接口===============");
        currencyAccountMapper.cancelIncrease(accountDTO);
        return Boolean.TRUE;
    }

}
