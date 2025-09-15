package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.user.UserRecommendRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.UserRecommendListVO;
import com.yupi.springbootinit.model.vo.UserRecommendVO;
import com.yupi.springbootinit.service.UserRecommendService;
import com.yupi.springbootinit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户推荐接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/user/recommend")
@Slf4j
@Api(tags = "用户推荐接口")
public class UserRecommendController {
    
    @Resource
    private UserRecommendService userRecommendService;
    
    @Resource
    private UserService userService;
    
    /**
     * 根据用户标签推荐相似用户
     *
     * @param userId 目标用户ID
     * @param limit 推荐数量限制（可选，默认10）
     * @param minSimilarity 最小相似度阈值（可选，默认0.1）
     * @param request HTTP请求对象
     * @return 推荐用户列表
     */
    @GetMapping("/by-tags")
    @ApiOperation(value = "根据用户标签推荐相似用户")
    public BaseResponse<List<UserRecommendVO>> recommendUsersByTags(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit,
            @RequestParam(value = "minSimilarity", defaultValue = "0.1") Double minSimilarity,
            HttpServletRequest request) {
        
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 权限校验：用户只能查看自己的推荐或管理员可以查看所有用户的推荐
        User loginUser = userService.getLoginUser(request);
        if (!userId.equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看其他用户的推荐");
        }
        
        List<UserRecommendVO> recommendations = userRecommendService.recommendUsersByTags(userId, limit, minSimilarity);
        return ResultUtils.success(recommendations);
    }
    
    /**
     * 获取当前登录用户的推荐
     *
     * @param limit 推荐数量限制（可选，默认10）
     * @param minSimilarity 最小相似度阈值（可选，默认0.1）
     * @param request HTTP请求对象
     * @return 推荐用户列表
     */
    @GetMapping("/my")
    @ApiOperation(value = "获取当前用户的推荐")
    public BaseResponse<List<UserRecommendVO>> getMyRecommendations(
            @RequestParam(value = "limit", defaultValue = "5") Integer limit,
            @RequestParam(value = "minSimilarity", defaultValue = "0.1") Double minSimilarity,
            HttpServletRequest request) {
        
        User loginUser = userService.getLoginUser(request);
        List<UserRecommendVO> recommendations = userRecommendService.recommendUsersByTags(
                loginUser.getId(), limit, minSimilarity);
        return ResultUtils.success(recommendations);
    }
    
    /**
     * 根据推荐请求获取推荐结果（POST方式，支持更复杂的参数）
     *
     * @param recommendRequest 推荐请求参数
     * @param request HTTP请求对象
     * @return 推荐结果
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取用户推荐列表")
    public BaseResponse<UserRecommendListVO> getRecommendations(
            @RequestBody UserRecommendRequest recommendRequest,
            HttpServletRequest request) {
        
        if (recommendRequest == null || recommendRequest.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        // 权限校验
        User loginUser = userService.getLoginUser(request);
        if (!recommendRequest.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看其他用户的推荐");
        }
        
        UserRecommendListVO result = userRecommendService.getRecommendations(recommendRequest);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取当前用户的推荐列表（POST方式）
     *
     * @param recommendRequest 推荐请求参数（userId会被忽略，使用当前登录用户ID）
     * @param request HTTP请求对象
     * @return 推荐结果
     */
    @PostMapping("/my/list")
    @ApiOperation(value = "获取当前用户的推荐列表")
    public BaseResponse<UserRecommendListVO> getMyRecommendationsList(
            @RequestBody UserRecommendRequest recommendRequest,
            HttpServletRequest request) {
        
        if (recommendRequest == null) {
            recommendRequest = new UserRecommendRequest();
        }
        
        User loginUser = userService.getLoginUser(request);
        recommendRequest.setUserId(loginUser.getId());
        
        UserRecommendListVO result = userRecommendService.getRecommendations(recommendRequest);
        return ResultUtils.success(result);
    }
    
    /**
     * 计算两个用户之间的标签相似度（管理员接口）
     *
     * @param userId1 用户1的ID
     * @param userId2 用户2的ID
     * @param request HTTP请求对象
     * @return 相似度评分
     */
    @GetMapping("/similarity")
    @ApiOperation(value = "计算用户标签相似度")
    public BaseResponse<Double> calculateUserSimilarity(
            @RequestParam("userId1") Long userId1,
            @RequestParam("userId2") Long userId2,
            HttpServletRequest request) {
        
        // 管理员权限校验
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行此操作");
        }
        
        if (userId1 == null || userId2 == null || userId1 <= 0 || userId2 <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 获取用户信息
        User user1 = userService.getById(userId1);
        User user2 = userService.getById(userId2);
        
        if (user1 == null || user2 == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        // 解析标签并计算相似度
        List<String> tags1 = userRecommendService.parseUserTags(user1.getTags());
        List<String> tags2 = userRecommendService.parseUserTags(user2.getTags());
        
        Double similarity = userRecommendService.calculateTagSimilarity(tags1, tags2);
        return ResultUtils.success(similarity);
    }
}