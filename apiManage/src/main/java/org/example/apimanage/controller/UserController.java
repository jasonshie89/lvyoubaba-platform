package org.example.apimanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.dto.R;
import org.example.apimanage.entity.User;
import org.example.apimanage.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表 Controller（后台管理版）
 */
@RestController
@RequestMapping("/admin/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询用户列表（后台管理用）
     */
    @GetMapping("/page")
    public R<Page<User>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        
        // 关键字搜索（用户名或昵称）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("username", keyword).or().like("nickname", keyword));
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc("create_time");
        
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        return R.ok(userPage);
    }

    /**
     * 获取所有用户（不分页）
     */
    @GetMapping("/list")
    public R<List<User>> list() {
        List<User> users = userMapper.selectList(null);
        return R.ok(users);
    }

    /**
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        return user != null ? R.ok(user) : R.error("用户不存在");
    }

    /**
     * 创建用户
     */
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody User user) {
        boolean result = userMapper.insert(user) > 0;
        return result ? R.ok(true) : R.error();
    }

    /**
     * 更新用户
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody User user) {
        boolean result = userMapper.updateById(user) > 0;
        return result ? R.ok(true) : R.error();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = userMapper.deleteById(id) > 0;
        return result ? R.ok(true) : R.error();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch-delete")
    public R<Boolean> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            userMapper.deleteById(id);
        }
        return R.ok(true);
    }
}