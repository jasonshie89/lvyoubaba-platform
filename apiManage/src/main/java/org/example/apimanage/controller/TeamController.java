package org.example.apimanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.dto.R;
import org.example.apimanage.entity.Team;
import org.example.apimanage.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamMapper teamMapper;

    @GetMapping("/page")
    public R<Page<Team>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<Team> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("destination", keyword).or().like("title", keyword));
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        Page<Team> teamPage = teamMapper.selectPage(page, wrapper);
        return R.ok(teamPage);
    }

    @GetMapping("/list")
    public R<List<Team>> list() {
        List<Team> teams = teamMapper.selectList(null);
        return R.ok(teams);
    }

    @GetMapping("/{id}")
    public R<Team> getById(@PathVariable Long id) {
        Team team = teamMapper.selectById(id);
        return team != null ? R.ok(team) : R.error("约伴不存在");
    }

    @GetMapping("/by-destination/{destination}")
    public R<List<Team>> getByDestination(@PathVariable String destination) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("destination", destination);
        List<Team> teams = teamMapper.selectList(queryWrapper);
        return R.ok(teams);
    }

    @GetMapping("/recruiting")
    public R<List<Team>> getRecruitingTeams() {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1).orderByDesc("create_time");
        List<Team> teams = teamMapper.selectList(queryWrapper);
        return R.ok(teams);
    }

    @PostMapping("/save")
    public R<Boolean> save(@RequestBody Team team) {
        boolean result = teamMapper.insert(team) > 0;
        return result ? R.ok(true) : R.error();
    }

    @PutMapping("/update")
    public R<Boolean> update(@RequestBody Team team) {
        boolean result = teamMapper.updateById(team) > 0;
        return result ? R.ok(true) : R.error();
    }

    @DeleteMapping("/delete/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = teamMapper.deleteById(id) > 0;
        return result ? R.ok(true) : R.error();
    }
}
