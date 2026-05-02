package org.example.apimanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.apimanage.entity.ActivityComment;

/**
 * 动态评论表 Mapper 接口
 */
@Mapper
public interface ActivityCommentMapper extends BaseMapper<ActivityComment> {
}