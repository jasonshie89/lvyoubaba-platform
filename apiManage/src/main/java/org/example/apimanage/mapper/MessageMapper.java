package org.example.apimanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.apimanage.entity.Message;

/**
 * 私信留言表 Mapper 接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}