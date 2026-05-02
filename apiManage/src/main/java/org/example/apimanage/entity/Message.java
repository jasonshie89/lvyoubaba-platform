package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 私信留言表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者用户 ID
     */
    private Long senderId;

    /**
     * 接收者用户 ID
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 图片 URL
     */
    private String imageUrl;

    /**
     * 关联约伴 ID
     */
    private Long teamId;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer isDelete;

    /**
     * 发送时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}