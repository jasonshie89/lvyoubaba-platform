package org.example.apimanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.entity.Activity;
import org.example.apimanage.entity.ActivityComment;
import org.example.apimanage.entity.ActivityLike;
import org.example.apimanage.mapper.ActivityCommentMapper;
import org.example.apimanage.mapper.ActivityLikeMapper;
import org.example.apimanage.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态服务类
 */
@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityLikeMapper activityLikeMapper;

    @Autowired
    private ActivityCommentMapper activityCommentMapper;

    /**
     * 分页查询动态列表
     */
    public Page<Activity> page(Integer pageNum, Integer pageSize, Long userId, Long scenicSpotId, Long teamId) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();

        if (userId != null) {
            wrapper.eq("user_id", userId);
        }

        if (scenicSpotId != null) {
            wrapper.eq("scenic_spot_id", scenicSpotId);
        }

        if (teamId != null) {
            wrapper.eq("team_id", teamId);
        }

        wrapper.eq("is_delete", 0);
        wrapper.orderByDesc("is_top");
        wrapper.orderByDesc("create_time");
        return activityMapper.selectPage(page, wrapper);
    }

    /**
     * 获取最新动态列表
     */
    public List<Activity> getLatest() {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 20");
        return activityMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询动态
     */
    public Activity getById(Long id) {
        return activityMapper.selectById(id);
    }

    /**
     * 创建动态
     */
    public boolean save(Activity activity) {
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        if (activity.getLikeCount() == null) {
            activity.setLikeCount(0);
        }
        if (activity.getCommentCount() == null) {
            activity.setCommentCount(0);
        }
        if (activity.getShareCount() == null) {
            activity.setShareCount(0);
        }
        if (activity.getViewCount() == null) {
            activity.setViewCount(0);
        }
        if (activity.getIsTop() == null) {
            activity.setIsTop(0);
        }
        if (activity.getIsDelete() == null) {
            activity.setIsDelete(0);
        }
        return activityMapper.insert(activity) > 0;
    }

    /**
     * 更新动态
     */
    public boolean update(Activity activity) {
        activity.setUpdateTime(LocalDateTime.now());
        return activityMapper.updateById(activity) > 0;
    }

    /**
     * 删除动态（逻辑删除）
     */
    public boolean delete(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity != null) {
            activity.setIsDelete(1);
            return activityMapper.updateById(activity) > 0;
        }
        return false;
    }

    /**
     * 增加浏览量
     */
    public boolean incrementViewCount(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity != null) {
            activity.setViewCount(activity.getViewCount() + 1);
            return activityMapper.updateById(activity) > 0;
        }
        return false;
    }

    /**
     * 点赞/取消点赞
     */
    public boolean toggleLike(Long activityId, Long userId) {
        QueryWrapper<ActivityLike> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId);
        wrapper.eq("user_id", userId);
        ActivityLike like = activityLikeMapper.selectOne(wrapper);

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return false;
        }

        if (like != null) {
            activityLikeMapper.deleteById(like.getId());
            activity.setLikeCount(Math.max(0, activity.getLikeCount() - 1));
        } else {
            like = new ActivityLike();
            like.setActivityId(activityId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            activityLikeMapper.insert(like);
            activity.setLikeCount(activity.getLikeCount() + 1);
        }

        return activityMapper.updateById(activity) > 0;
    }

    /**
     * 添加评论
     */
    public boolean addComment(Long activityId, Long userId, String content) {
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(activityId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        int result = activityCommentMapper.insert(comment);
        if (result > 0) {
            Activity activity = activityMapper.selectById(activityId);
            activity.setCommentCount(activity.getCommentCount() + 1);
            activityMapper.updateById(activity);
        }

        return result > 0;
    }

    /**
     * 获取评论列表
     */
    public List<ActivityComment> getComments(Long activityId) {
        QueryWrapper<ActivityComment> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId);
        wrapper.orderByDesc("create_time");
        return activityCommentMapper.selectList(wrapper);
    }

    /**
     * 检查用户是否已点赞
     */
    public boolean hasLiked(Long activityId, Long userId) {
        QueryWrapper<ActivityLike> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId);
        wrapper.eq("user_id", userId);
        return activityLikeMapper.selectCount(wrapper) > 0;
    }
}
