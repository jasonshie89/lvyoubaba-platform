package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态评论表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("activity_comment")
public class ActivityComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 动态 ID
     */
    private Long activityId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 父评论 ID
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer isDelete;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}