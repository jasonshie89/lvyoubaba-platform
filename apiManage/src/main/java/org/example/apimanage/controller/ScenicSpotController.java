package org.example.apimanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.dto.R;
import org.example.apimanage.entity.ScenicSpot;
import org.example.apimanage.mapper.ScenicSpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenic-spot")
public class ScenicSpotController {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @GetMapping("/page")
    public R<Page<ScenicSpot>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Integer isHot) {
        Page<ScenicSpot> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ScenicSpot> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("name", keyword);
        }
        if (StringUtils.hasText(province)) {
            wrapper.eq("province", province);
        }
        if (isHot != null) {
            wrapper.eq("is_hot", isHot);
        }
        wrapper.orderByDesc("create_time");
        Page<ScenicSpot> scenicSpotPage = scenicSpotMapper.selectPage(page, wrapper);
        return R.ok(scenicSpotPage);
    }

    @GetMapping("/list")
    public R<List<ScenicSpot>> list() {
        List<ScenicSpot> spots = scenicSpotMapper.selectList(null);
        return R.ok(spots);
    }

    @GetMapping("/{id}")
    public R<ScenicSpot> getById(@PathVariable Long id) {
        ScenicSpot spot = scenicSpotMapper.selectById(id);
        return spot != null ? R.ok(spot) : R.error("景点不存在");
    }

    @GetMapping("/by-city/{city}")
    public R<List<ScenicSpot>> getByCity(@PathVariable String city) {
        QueryWrapper<ScenicSpot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city", city);
        List<ScenicSpot> spots = scenicSpotMapper.selectList(queryWrapper);
        return R.ok(spots);
    }

    @GetMapping("/hot")
    public R<List<ScenicSpot>> getHotSpots() {
        QueryWrapper<ScenicSpot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_hot", 1).orderByDesc("view_count");
        List<ScenicSpot> spots = scenicSpotMapper.selectList(queryWrapper);
        return R.ok(spots);
    }

    @PostMapping("/save")
    public R<Boolean> save(@RequestBody ScenicSpot spot) {
        boolean result = scenicSpotMapper.insert(spot) > 0;
        return result ? R.ok(true) : R.error();
    }

    @PutMapping("/update")
    public R<Boolean> update(@RequestBody ScenicSpot spot) {
        boolean result = scenicSpotMapper.updateById(spot) > 0;
        return result ? R.ok(true) : R.error();
    }

    @DeleteMapping("/delete/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = scenicSpotMapper.deleteById(id) > 0;
        return result ? R.ok(true) : R.error();
    }
}
