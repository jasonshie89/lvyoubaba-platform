package org.example.apimanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scenic_spot")
public class ScenicSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 景点名称
     */
    private String name;

    /**
     * 封面图片 URL
     */
    private String coverImage;

    /**
     * 图片集 URL
     */
    private String images;

    /**
     * 景点介绍
     */
    private String description;

    /**
     * 详细地址
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
     * 所属城市
     */
    private String city;

    /**
     * 所属省份
     */
    private String province;

    /**
     * 门票价格
     */
    private BigDecimal ticketPrice;

    /**
     * 开放时间
     */
    private String openingHours;

    /**
     * 最佳游玩季节
     */
    private String bestSeason;

    /**
     * 建议游玩时长（分钟）
     */
    private Integer playDuration;

    /**
     * 标签
     */
    private String tags;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 是否热门 0-否 1-是
     */
    private Integer isHot;

    /**
     * 是否推荐 0-否 1-是
     */
    private Integer isRecommend;

    /**
     * 状态 0-下架 1-上架
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