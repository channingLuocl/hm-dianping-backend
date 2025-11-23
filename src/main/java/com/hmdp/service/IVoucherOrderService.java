package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {
    /**
     * 秒杀券下单
     *
     * @param voucherId
     * @return
     */
    Result seckillVoucher(Long voucherId);

    /**
     * 真正创建订单的函数
     *
     * @param voucherId
     * @return
     */
    Result createVoucherOrder(Long voucherId);

    /**
     * 秒杀券下单,异步优化写法
     *
     * @param voucherId
     * @return
     */
    Result seckillVoucherAsync(Long voucherId);
}
