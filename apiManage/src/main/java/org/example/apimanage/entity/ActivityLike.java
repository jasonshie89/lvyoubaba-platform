package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态点赞表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("activity_like")
public class ActivityLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 动态 ID
     */
    private Long activityId;

    /**
     * 点赞用户 ID
     */
    private Long userId;

    /**
     * 点赞时间
     */
    private LocalDateTime createTime;
}