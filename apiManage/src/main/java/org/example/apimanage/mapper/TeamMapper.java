package org.example.apimanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.apimanage.entity.Team;

/**
 * 约伴表 Mapper 接口
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {
}