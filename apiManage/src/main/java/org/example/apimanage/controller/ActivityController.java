package org.example.apimanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.dto.R;
import org.example.apimanage.entity.Activity;
import org.example.apimanage.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityMapper activityMapper;

    @GetMapping("/page")
    public R<Page<Activity>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("content", keyword);
        }
        // 只查询未删除的数据 (is_delete 为 null 或 0)
        wrapper.and(w -> w.isNull("is_delete").or().eq("is_delete", 0));
        wrapper.orderByDesc("create_time");
        Page<Activity> activityPage = activityMapper.selectPage(page, wrapper);
        System.out.println("查询到动态数量: " + activityPage.getTotal());
        return R.ok(activityPage);
    }

    @GetMapping("/list")
    public R<List<Activity>> list() {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        // 只查询未删除的数据
        wrapper.and(w -> w.isNull("is_delete").or().eq("is_delete", 0));
        wrapper.orderByDesc("create_time");
        List<Activity> activities = activityMapper.selectList(wrapper);
        return R.ok(activities);
    }

    @GetMapping("/{id}")
    public R<Activity> getById(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        return activity != null ? R.ok(activity) : R.error("动态不存在");
    }

    @GetMapping("/by-user/{userId}")
    public R<List<Activity>> getByUserId(@PathVariable Long userId) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        List<Activity> activities = activityMapper.selectList(queryWrapper);
        return R.ok(activities);
    }

    @GetMapping("/hot")
    public R<List<Activity>> getHotActivities() {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("like_count", "create_time");
        List<Activity> activities = activityMapper.selectList(queryWrapper);
        return R.ok(activities);
    }

    @PostMapping("/save")
    public R<Boolean> save(@RequestBody Activity activity) {
        System.out.println("收到保存请求, activity: " + activity);
        // 设置默认值
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
        int result = activityMapper.insert(activity);
        System.out.println("插入结果: " + result + ", 插入后的ID: " + activity.getId());
        return result > 0 ? R.ok(true) : R.error("保存失败");
    }

    @PutMapping("/update")
    public R<Boolean> update(@RequestBody Activity activity) {
        boolean result = activityMapper.updateById(activity) > 0;
        return result ? R.ok(true) : R.error();
    }

    @DeleteMapping("/delete/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = activityMapper.deleteById(id) > 0;
        return result ? R.ok(true) : R.error();
    }
}
