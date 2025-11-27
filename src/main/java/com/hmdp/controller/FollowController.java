package com.hmdp.controller;


import com.hmdp.dto.Result;
import com.hmdp.service.IFollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/follow")
@Api("关注相关接口")
public class FollowController {
    @Resource
    private IFollowService followService;

    @PutMapping("/{id}/{isFollow}")
    @ApiOperation("关注/取关")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    @GetMapping("/or/not/{id}")
    @ApiOperation("判断是否关注")
    public Result isFollow(@PathVariable("id") Long followUserid) {
        return followService.isFollow(followUserid);
    }

    @GetMapping("/common/{id}")
    @ApiOperation("获得共同关注列表")
    public Result followCommons(@PathVariable("id") Long id) {
        return followService.followCommons(id);
    }
}
