package org.example.apimanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.entity.ScenicSpot;
import org.example.apimanage.mapper.ScenicSpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点服务类
 */
@Service
public class ScenicSpotService {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    /**
     * 分页查询景点列表
     */
    public Page<ScenicSpot> page(Integer pageNum, Integer pageSize, String keyword, String province, String city, String type) {
        Page<ScenicSpot> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ScenicSpot> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("name", keyword).or().like("address", keyword));
        }

        if (StringUtils.hasText(province)) {
            wrapper.eq("province", province);
        }

        if (StringUtils.hasText(city)) {
            wrapper.eq("city", city);
        }

        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }

        wrapper.orderByDesc("create_time");
        return scenicSpotMapper.selectPage(page, wrapper);
    }

    /**
     * 获取热门景点列表
     */
    public List<ScenicSpot> getHotSpots() {
        QueryWrapper<ScenicSpot> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("view_count");
        wrapper.last("LIMIT 10");
        return scenicSpotMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询景点
     */
    public ScenicSpot getById(Long id) {
        return scenicSpotMapper.selectById(id);
    }

    /**
     * 创建景点
     */
    public boolean save(ScenicSpot scenicSpot) {
        scenicSpot.setCreateTime(LocalDateTime.now());
        scenicSpot.setUpdateTime(LocalDateTime.now());
        if (scenicSpot.getViewCount() == null) {
            scenicSpot.setViewCount(0);
        }
        if (scenicSpot.getLikeCount() == null) {
            scenicSpot.setLikeCount(0);
        }
        if (scenicSpot.getStatus() == null) {
            scenicSpot.setStatus(1);
        }
        return scenicSpotMapper.insert(scenicSpot) > 0;
    }

    /**
     * 更新景点
     */
    public boolean update(ScenicSpot scenicSpot) {
        scenicSpot.setUpdateTime(LocalDateTime.now());
        return scenicSpotMapper.updateById(scenicSpot) > 0;
    }

    /**
     * 删除景点
     */
    public boolean delete(Long id) {
        return scenicSpotMapper.deleteById(id) > 0;
    }

    /**
     * 增加浏览量
     */
    public boolean incrementViewCount(Long id) {
        ScenicSpot spot = scenicSpotMapper.selectById(id);
        if (spot != null) {
            spot.setViewCount(spot.getViewCount() + 1);
            return scenicSpotMapper.updateById(spot) > 0;
        }
        return false;
    }

    /**
     * 增加点赞数
     */
    public boolean incrementLikeCount(Long id) {
        ScenicSpot spot = scenicSpotMapper.selectById(id);
        if (spot != null) {
            spot.setLikeCount(spot.getLikeCount() + 1);
            return scenicSpotMapper.updateById(spot) > 0;
        }
        return false;
    }
}
