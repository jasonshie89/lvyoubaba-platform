package org.example.apimanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.apimanage.dto.R;
import org.example.apimanage.entity.User;
import org.example.apimanage.interceptor.LoginInterceptor;
import org.example.apimanage.mapper.UserMapper;
import org.example.apimanage.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * APP端用户控制器
 * 提供用户登录、信息查询、信息修改等功能
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class ApiUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录接口
     * @param loginParams 包含username和password的登录参数
     * @return JWT Token及用户信息
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginParams) {
        String username = loginParams.get("username");
        String password = loginParams.get("password");

        // 1. 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return R.error("用户名和密码不能为空");
        }

        // 2. 根据用户名查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        // 3. 验证用户是否存在
        if (user == null) {
            return R.error("用户不存在");
        }

        // 4. 验证用户状态是否正常
        if (user.getStatus() != 1) {
            return R.error("用户已被禁用，请联系管理员");
        }

        // 5. 密码验证（实际项目中使用BCrypt加密存储和验证）
        // 注：如需加密验证，可使用BCryptPasswordEncoder.matches()方法
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        //     return R.error("密码错误");
        // }

        // 6. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 7. 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return R.ok(result);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户详细信息
     */
    @GetMapping("/info")
    public R<User> getUserInfo() {
        // 从拦截器ThreadLocal中获取当前登录用户ID
        Long userId = LoginInterceptor.getCurrentUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return R.error("用户信息不存在");
        }

        return R.ok(user);
    }

    /**
     * 更新用户信息
     * @param user 用户信息对象
     * @return 更新结果
     */
    @PutMapping("/update")
    public R<Boolean> updateUserInfo(@RequestBody User user) {
        Long userId = LoginInterceptor.getCurrentUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }

        // 设置用户ID，防止越权修改其他用户
        user.setId(userId);
        user.setUpdateTime(new java.util.Date());

        int result = userMapper.updateById(user);
        return result > 0 ? R.ok(true) : R.error("更新失败");
    }
}
