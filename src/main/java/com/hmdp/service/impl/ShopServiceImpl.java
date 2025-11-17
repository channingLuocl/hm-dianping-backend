package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据id查询商铺信息
     *
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @Override
    public Result queryShopById(Long id) {
//        1.从redis查询商铺缓存的商铺信息
        String cacheKey = CACHE_SHOP_KEY + id;
        Shop shop = new Shop();
        boolean keyExists = stringRedisTemplate.hasKey(cacheKey);
        if (keyExists) {  //用缓存空对象的方式，解决缓存穿透
            Map<Object, Object> shopMap = stringRedisTemplate.opsForHash().entries(cacheKey);
            // 2. 判断是否是空对象缓存（仅包含占位字段）
            if (shopMap.size() == 1 && "empty".equals(shopMap.keySet().iterator().next())) {
                log.info("命中空对象缓存，直接返回：{}", cacheKey);
                return Result.fail("店铺不存在");
            }
            // 3. 是正常缓存数据，转Bean返回
            shop = BeanUtil.fillBeanWithMap(shopMap, new Shop(), false);
            log.info("命中正常店铺缓存，返回数据：{}", shop.getId());
            return Result.ok(shop);
        }
//        3.redis没有缓存的话，去查数据库,用到mybatis-plus的写法
        log.info("Redis无缓存，查询数据库，店铺ID：{}", id);
        shop = getById(id);
//        4.判断数据库中能否正确查出来
        if (shop == null) {
            //用缓存空对象的方式，解决缓存穿透,放入一个空的map对象
            Map<Object, Object> nullCacheMap = new HashMap<>();
            nullCacheMap.put("empty", "1"); // 关键：添加占位字段
            stringRedisTemplate.opsForHash().putAll(cacheKey, nullCacheMap);
            stringRedisTemplate.expire(cacheKey, CACHE_NULL_TTL, TimeUnit.MINUTES); // 空对象缓存时间更短
            log.info("数据库无此店铺，写入空对象缓存（带占位字段）：{}", cacheKey);
            return Result.fail("店铺不存在");
        } else {  //能查出来就，把信息放入redis并返回前端
            Map<Object, Object> shopMap = new HashMap<>();
            shopMap = new HashMap<>();
            shopMap.put("id", shop.getId().toString());
            shopMap.put("name", shop.getName());
            shopMap.put("typeId", shop.getTypeId().toString());
            shopMap.put("images", shop.getImages());
            shopMap.put("area", shop.getArea());
            shopMap.put("address", shop.getAddress());
            shopMap.put("x", shop.getX().toString());
            shopMap.put("y", shop.getY().toString());
            shopMap.put("avgPrice", shop.getAvgPrice().toString());
            shopMap.put("sold", shop.getSold().toString());
            shopMap.put("comments", shop.getComments().toString());
            shopMap.put("score", shop.getScore().toString());
            shopMap.put("openHours", shop.getOpenHours().toString());
            shopMap.put("createTime", shop.getCreateTime().toString());
            shopMap.put("updateTime", shop.getUpdateTime().toString());
            stringRedisTemplate.opsForHash().putAll(cacheKey, shopMap);
            stringRedisTemplate.expire(cacheKey, CACHE_SHOP_TTL, TimeUnit.MINUTES);
            return Result.ok(shop);
        }
    }

    /**
     * 更新商铺信息
     *
     * @param shop 商铺数据
     * @return 无
     */
    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        updateById(shop);  // 用到mybatis-plus的https://baomidou.com/guides/data-interface/#update-1
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());  //更新数据库后删除redis缓存，确保mysql和redis的一致性，见笔记redis 3.1缓存更新策略
        return Result.ok();
    }
}
