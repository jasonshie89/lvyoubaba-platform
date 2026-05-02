package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 约伴表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("team")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发起人用户 ID
     */
    private Long userId;

    /**
     * 约伴标题
     */
    private String title;

    /**
     * 约伴详情描述
     */
    private String content;

    /**
     * 关联景点 ID
     */
    private Long scenicSpotId;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 出发时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 预计天数
     */
    private Integer duration;

    /**
     * 最大人数
     */
    private Integer maxMembers;

    /**
     * 当前人数
     */
    private Integer currentMembers;

    /**
     * 预算（人均）
     */
    private BigDecimal budget;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 难度等级 1-简单 2-中等 3-困难
     */
    private Integer difficultyLevel;

    /**
     * 参与要求
     */
    private String requirements;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 报名人数
     */
    private Integer signupCount;

    /**
     * 是否已满员 0-否 1-是
     */
    private Integer isFull;

    /**
     * 状态 0-取消 1-招募中 2-进行中 3-已完成
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}