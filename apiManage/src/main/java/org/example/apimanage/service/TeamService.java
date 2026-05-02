package org.example.apimanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.apimanage.entity.Team;
import org.example.apimanage.entity.TeamSignup;
import org.example.apimanage.mapper.TeamMapper;
import org.example.apimanage.mapper.TeamSignupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 约伴服务类
 */
@Service
public class TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamSignupMapper teamSignupMapper;

    /**
     * 分页查询约伴列表
     */
    public Page<Team> page(Integer pageNum, Integer pageSize, String keyword, Integer status, Long userId) {
        Page<Team> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Team> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("destination", keyword));
        }

        if (status != null) {
            wrapper.eq("status", status);
        }

        if (userId != null) {
            wrapper.eq("user_id", userId);
        }

        wrapper.orderByDesc("create_time");
        return teamMapper.selectPage(page, wrapper);
    }

    /**
     * 获取招募中的约伴列表
     */
    public List<Team> getRecruiting() {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.eq("is_full", 0);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 10");
        return teamMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询约伴
     */
    public Team getById(Long id) {
        return teamMapper.selectById(id);
    }

    /**
     * 创建约伴
     */
    public boolean save(Team team) {
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());
        if (team.getStatus() == null) {
            team.setStatus(1);
        }
        if (team.getCurrentMembers() == null) {
            team.setCurrentMembers(1);
        }
        if (team.getIsFull() == null) {
            team.setIsFull(0);
        }
        if (team.getViewCount() == null) {
            team.setViewCount(0);
        }
        if (team.getSignupCount() == null) {
            team.setSignupCount(0);
        }
        return teamMapper.insert(team) > 0;
    }

    /**
     * 更新约伴
     */
    public boolean update(Team team) {
        team.setUpdateTime(LocalDateTime.now());
        return teamMapper.updateById(team) > 0;
    }

    /**
     * 删除约伴
     */
    public boolean delete(Long id) {
        return teamMapper.deleteById(id) > 0;
    }

    /**
     * 增加浏览量
     */
    public boolean incrementViewCount(Long id) {
        Team team = teamMapper.selectById(id);
        if (team != null) {
            team.setViewCount(team.getViewCount() + 1);
            return teamMapper.updateById(team) > 0;
        }
        return false;
    }

    /**
     * 报名约伴
     */
    public boolean signup(Long teamId, Long userId, String message) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            return false;
        }

        if (team.getIsFull() == 1) {
            return false;
        }

        TeamSignup signup = new TeamSignup();
        signup.setTeamId(teamId);
        signup.setUserId(userId);
        signup.setMessage(message);
        signup.setStatus(1);
        signup.setCreateTime(LocalDateTime.now());
        signup.setUpdateTime(LocalDateTime.now());

        int result = teamSignupMapper.insert(signup);
        if (result > 0) {
            team.setSignupCount(team.getSignupCount() + 1);
            teamMapper.updateById(team);
        }

        return result > 0;
    }

    /**
     * 查询报名列表
     */
    public List<TeamSignup> getSignupList(Long teamId) {
        QueryWrapper<TeamSignup> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        wrapper.orderByDesc("create_time");
        return teamSignupMapper.selectList(wrapper);
    }

    /**
     * 检查用户是否已报名
     */
    public boolean hasSignedUp(Long teamId, Long userId) {
        QueryWrapper<TeamSignup> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        wrapper.eq("user_id", userId);
        return teamSignupMapper.selectCount(wrapper) > 0;
    }
}
