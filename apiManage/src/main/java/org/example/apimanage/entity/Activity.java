package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 动态表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布者用户 ID
     */
    private Long userId;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 图片集 URL
     */
    private String images;

    /**
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 关联景点 ID
     */
    private Long scenicSpotId;

    /**
     * 关联约伴 ID
     */
    private Long teamId;

    /**
     * 发布地点
     */
    private String location;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 预设标签
     */
    private String tags;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer isTop;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}