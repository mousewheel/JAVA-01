package org.dromara.hmily.demo.common.account.api;

import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.demo.common.account.dto.AccountDTO;

/**
 * @Author zhurui
 * @Date 2021/3/16 12:57 下午
 * @Version 1.0
 */
public interface CurrencyAccountService {

    @Hmily
    boolean increase(AccountDTO accountDTO);


    @Hmily
    boolean decrease(AccountDTO accountDTO);

}
