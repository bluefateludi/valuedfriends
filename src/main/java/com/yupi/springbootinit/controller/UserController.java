package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.user.UserAddRequest;
import com.yupi.springbootinit.model.dto.user.UserLoginRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserRegisterRequest;
import com.yupi.springbootinit.model.dto.user.UserUpdateMyRequest;
import com.yupi.springbootinit.model.dto.user.UserUpdateRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.LoginUserVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.UserService;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
// 移除未使用的微信相关导入
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.yupi.springbootinit.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     * 
     * @param userRegisterRequest 用户注册请求参数，包含用户账号、密码和确认密码
     * @return 注册成功返回用户ID，失败抛出异常
     * @throws BusinessException 当参数为空或注册失败时抛出
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码和确认密码不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * 
     * @param userLoginRequest 用户登录请求参数，包含用户账号和密码
     * @param request          HTTP请求对象，用于设置登录状态
     * @return 登录成功返回脱敏后的用户信息
     * @throws BusinessException 当参数为空、用户不存在或密码错误时抛出
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号和密码不能为空");
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     * 
     * @param request HTTP请求对象，用于清除登录状态
     * @return 注销成功返回true
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     * 
     * @param request HTTP请求对象，用于获取当前登录状态
     * @return 当前登录用户的脱敏信息
     * @throws BusinessException 当用户未登录时抛出
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户（管理员功能）
     * 
     * @param userAddRequest 用户创建请求参数
     * @param request        HTTP请求对象，用于权限验证
     * @return 创建成功返回用户ID
     * @throws BusinessException 当参数无效、权限不足或创建失败时抛出
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        // 验证管理员权限
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行此操作");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建用户失败");
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户（管理员功能）
     * 
     * @param deleteRequest 删除请求参数，包含要删除的用户ID
     * @param request       HTTP请求对象，用于权限验证
     * @return 删除成功返回true
     * @throws BusinessException 当参数无效、权限不足或删除失败时抛出
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除ID无效");
        }
        // 验证管理员权限
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行此操作");
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户（管理员功能）
     * 
     * @param userUpdateRequest 用户更新请求参数
     * @param request           HTTP请求对象，用于权限验证
     * @return 更新成功返回true
     * @throws BusinessException 当参数无效、权限不足或更新失败时抛出
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新参数或用户ID不能为空");
        }
        // 验证管理员权限
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行此操作");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新用户失败");
        return ResultUtils.success(true);
    }

    /**
     * 根据ID获取用户详细信息（仅管理员）
     * 
     * @param id      用户ID
     * @param request HTTP请求对象，用于权限验证
     * @return 用户详细信息
     * @throws BusinessException 当参数无效、权限不足或用户不存在时抛出
     */
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID必须大于0");
        }
        // 验证管理员权限
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行此操作");
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ResultUtils.success(user);
    }

    /**
     * 根据ID获取用户脱敏信息
     * 
     * @param id      用户ID，必须大于0
     * @param request HTTP请求对象
     * @return 用户脱敏信息
     * @throws BusinessException 当用户ID无效或用户不存在时抛出
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID必须大于0");
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     * 
     * @param userQueryRequest 用户查询请求参数，包含分页信息和查询条件
     * @param request          HTTP请求对象，用于权限验证
     * @return 分页用户列表
     * @throws BusinessException 当权限不足时抛出
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        }
        // 验证管理员权限
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行此操作");
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户脱敏信息列表
     * 
     * @param userQueryRequest 用户查询请求参数，包含分页信息和查询条件
     * @param request          HTTP请求对象
     * @return 分页用户脱敏信息列表
     * @throws BusinessException 当参数无效时抛出
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR, "分页大小不能超过20");
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     * 
     * @param userUpdateMyRequest 个人信息更新请求参数
     * @param request             HTTP请求对象，用于获取当前登录用户
     * @return 更新成功返回true
     * @throws BusinessException 当参数无效、用户未登录或更新失败时抛出
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新参数不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新个人信息失败");
        return ResultUtils.success(true);
    }
}
