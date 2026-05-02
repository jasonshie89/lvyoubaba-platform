package org.example.apimanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.apimanage.entity.ActivityLike;

/**
 * 动态点赞表 Mapper 接口
 */
@Mapper
public interface ActivityLikeMapper extends BaseMapper<ActivityLike> {
}