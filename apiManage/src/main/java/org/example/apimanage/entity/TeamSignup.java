package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 约伴报名表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("team_signup")
public class TeamSignup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 约伴 ID
     */
    private Long teamId;

    /**
     * 报名用户 ID
     */
    private Long userId;

    /**
     * 留言/备注
     */
    private String message;

    /**
     * 状态 0-拒绝 1-待确认 2-已加入 3-已取消
     */
    private Integer status;

    /**
     * 报名时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}