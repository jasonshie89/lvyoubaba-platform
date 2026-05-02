package org.example.apimanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.entity.User;
import org.example.apimanage.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询用户列表
     */
    public Page<User> page(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("username", keyword).or().like("nickname", keyword));
        }

        if (status != null) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("create_time");
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 获取所有用户
     */
    public List<User> list() {
        return userMapper.selectList(null);
    }

    /**
     * 根据ID查询用户
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据用户名查询用户
     */
    public User getByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 创建用户
     */
    public boolean save(User user) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        if (user.getUpdateTime() == null) {
            user.setUpdateTime(LocalDateTime.now());
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        return userMapper.insert(user) > 0;
    }

    /**
     * 更新用户
     */
    public boolean update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    /**
     * 删除用户
     */
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 批量删除用户
     */
    public boolean batchDelete(List<Long> ids) {
        return userMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("status", 1);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 微信登录/注册
     */
    public User wechatLogin(String openid, String nickname, String avatarUrl) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("wechat", openid);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            user = new User();
            user.setWechat(openid);
            user.setNickname(nickname);
            user.setAvatarUrl(avatarUrl);
            user.setUsername("wx_" + System.currentTimeMillis());
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        return user;
    }
}
