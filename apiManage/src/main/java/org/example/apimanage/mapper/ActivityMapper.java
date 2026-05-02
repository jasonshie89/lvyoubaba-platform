package org.example.apimanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.apimanage.entity.Activity;

/**
 * 动态表 Mapper 接口
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}