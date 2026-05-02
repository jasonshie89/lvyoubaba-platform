<template>
  <view class="page-container" v-if="detail">
    <!-- 封面区域 -->
    <view class="cover-section">
      <image :src="detail.coverImage || 'https://picsum.photos/750/500?random=' + detail.id" mode="aspectFill" class="cover-img" />
      <view class="cover-mask"></view>
      <view class="cover-info">
        <text class="scenic-name">{{ detail.name }}</text>
        <view class="location-row">
          <view class="location-icon"></view>
          <text class="location-text">{{ detail.province }} · {{ detail.city }}</text>
        </view>
      </view>
      <view class="back-btn" @click="goBack">
        <text>返回</text>
      </view>
    </view>

    <!-- 快捷信息 -->
    <view class="quick-info">
      <view class="info-item" v-if="detail.ticketPrice > 0">
        <text class="info-label">门票</text>
        <text class="info-value price">¥{{ detail.ticketPrice }}</text>
      </view>
      <view class="info-item" v-else>
        <text class="info-label">门票</text>
        <text class="info-value free">免费</text>
      </view>
      <view class="info-divider"></view>
      <view class="info-item">
        <text class="info-label">建议游玩</text>
        <text class="info-value">{{ formatDuration(detail.playDuration) }}</text>
      </view>
      <view class="info-divider"></view>
      <view class="info-item">
        <text class="info-label">浏览量</text>
        <text class="info-value">{{ formatCount(detail.viewCount) }}</text>
      </view>
    </view>

    <!-- 详细信息 -->
    <view class="detail-section">
      <!-- 景点介绍 -->
      <view class="section-card">
        <view class="card-title">景点介绍</view>
        <text class="card-content">{{ detail.description }}</text>
      </view>

      <!-- 游玩信息 -->
      <view class="section-card">
        <view class="card-title">游玩信息</view>
        <view class="info-list">
          <view class="info-row" v-if="detail.openingHours">
            <text class="row-label">开放时间</text>
            <text class="row-value">{{ detail.openingHours }}</text>
          </view>
          <view class="info-row" v-if="detail.bestSeason">
            <text class="row-label">最佳季节</text>
            <text class="row-value">{{ detail.bestSeason }}</text>
          </view>
          <view class="info-row" v-if="detail.location">
            <text class="row-label">详细地址</text>
            <text class="row-value">{{ detail.location }}</text>
          </view>
        </view>
      </view>

      <!-- 特色标签 -->
      <view class="section-card" v-if="detail.tags">
        <view class="card-title">特色标签</view>
        <view class="tag-list">
          <text class="tag-item" v-for="(tag, idx) in parseTags(detail.tags)" :key="idx">{{ tag }}</text>
        </view>
      </view>

      <!-- 数据统计 -->
      <view class="stats-card">
        <view class="stat-item">
          <text class="stat-value">{{ formatCount(detail.viewCount) }}</text>
          <text class="stat-label">浏览</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ formatCount(detail.likeCount) }}</text>
          <text class="stat-label">点赞</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ detail.isHot ? '热门' : '普通' }}</text>
          <text class="stat-label">热度</text>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bar-left">
        <view class="bar-btn" @click="toggleFavorite">
          <view class="bar-icon favorite-icon"></view>
          <text>收藏</text>
        </view>
        <view class="bar-btn" @click="share">
          <view class="bar-icon share-icon"></view>
          <text>分享</text>
        </view>
      </view>
      <view class="book-btn" @click="goToBook">
        <text>立即预订</text>
      </view>
    </view>
  </view>

  <!-- 加载状态 -->
  <view class="loading-page" v-else>
    <view class="loading-spinner"></view>
    <text class="loading-text">加载中</text>
  </view>
</template>

<script>
import { scenicApi } from '../../api/index.js'

export default {
  data() {
    return {
      id: null,
      detail: null
    }
  },
  onLoad(options) {
    this.id = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      try {
        const result = await scenicApi.getDetail(this.id)
        if (result) {
          this.detail = result
        } else {
          this.loadFallbackData()
        }
      } catch (error) {
        console.error('加载失败:', error)
        this.loadFallbackData()
      }
    },
    loadFallbackData() {
      const staticData = {
        1: {
          id: 1, name: '九寨沟', province: '四川', city: '阿坝',
          location: '四川省阿坝州九寨沟县漳扎镇', coverImage: 'https://picsum.photos/750/500?random=100',
          ticketPrice: 169, description: '九寨沟位于四川省阿坝藏族羌族自治州，是一条纵深50余千米的山沟谷地，因沟内有九个藏族村寨坐落在这片高山湖泊群中而得名。这里有神奇的翠海、叠瀑、彩林、雪峰和藏族风情，被誉为"童话世界"。',
          openingHours: '08:00-17:00', bestSeason: '9-10月', playDuration: 420,
          tags: '湖泊,瀑布,彩林', viewCount: 32100, likeCount: 3980, isHot: 1
        },
        2: {
          id: 2, name: '黄山', province: '安徽', city: '黄山',
          location: '安徽省黄山市黄山区', coverImage: 'https://picsum.photos/750/500?random=200',
          ticketPrice: 190, description: '黄山以奇松、怪石、云海、温泉、冬雪"五绝"著称于世，拥有"天下第一奇山"之称。主峰莲花峰海拔1864米，是中国最美的山岳景区之一。',
          openingHours: '06:00-17:30', bestSeason: '四季皆宜', playDuration: 480,
          tags: '登山,观景,日出', viewCount: 28560, likeCount: 3250, isHot: 1
        },
        3: {
          id: 3, name: '西湖', province: '浙江', city: '杭州',
          location: '浙江省杭州市西湖区', coverImage: 'https://picsum.photos/750/500?random=300',
          ticketPrice: 0, description: '西湖是中国大陆首批国家重点风景名胜区和中国十大风景名胜之一，是现今《世界遗产名录》中少数几个和中国唯一一个湖泊类文化遗产。',
          openingHours: '全天开放', bestSeason: '四季皆宜', playDuration: 180,
          tags: '湖泊,园林,文化', viewCount: 58960, likeCount: 6850, isHot: 1
        },
        4: {
          id: 4, name: '丽江古城', province: '云南', city: '丽江',
          location: '云南省丽江市古城区', coverImage: 'https://picsum.photos/750/500?random=400',
          ticketPrice: 50, description: '丽江古城是中国历史文化名城之一，是"保存最为完好的四大古城"之一。古城始建于宋末元初，是纳西族聚居地，拥有深厚的历史文化底蕴。',
          openingHours: '全天开放', bestSeason: '四季皆宜', playDuration: 240,
          tags: '古城,文化,摄影', viewCount: 35230, likeCount: 4120, isHot: 1
        }
      }
      this.detail = staticData[this.id] || staticData[1]
    },
    parseTags(tags) {
      if (!tags) return []
      return tags.split(',').filter(Boolean)
    },
    formatDuration(minutes) {
      if (!minutes) return '半天'
      if (minutes < 120) return minutes + '分钟'
      if (minutes < 480) return Math.ceil(minutes / 60) + '小时'
      return Math.ceil(minutes / 480) + '天'
    },
    formatCount(count) {
      if (!count) return '0'
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
      if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
      return count.toString()
    },
    goBack() {
      uni.navigateBack()
    },
    toggleFavorite() {
      uni.showToast({ title: '已收藏', icon: 'success' })
    },
    share() {
      uni.showShareMenu({
        withShareTicket: true,
        showShareItems: ['wechatFriends', 'wechatMoment']
      })
    },
    goToBook() {
      uni.showToast({ title: '预订功能开发中', icon: 'none' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #F5F6F8;
  padding-bottom: 140rpx;
}

/* 封面区域 */
.cover-section {
  position: relative;
  height: 500rpx;

  .cover-img {
    width: 100%;
    height: 100%;
  }

  .cover-mask {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 200rpx;
    background: linear-gradient(transparent, rgba(0,0,0,0.6));
  }

  .cover-info {
    position: absolute;
    bottom: 40rpx;
    left: 30rpx;
    right: 30rpx;

    .scenic-name {
      display: block;
      font-size: 44rpx;
      font-weight: 700;
      color: #fff;
      margin-bottom: 16rpx;
    }

    .location-row {
      display: flex;
      align-items: center;
      gap: 10rpx;

      .location-icon {
        width: 24rpx;
        height: 24rpx;
        border: 3rpx solid #fff;
        border-radius: 50%;
        position: relative;

        &::after {
          content: '';
          position: absolute;
          left: 50%;
          top: 50%;
          transform: translate(-50%, -50%);
          width: 8rpx;
          height: 8rpx;
          background: #fff;
          border-radius: 50%;
        }
      }

      .location-text {
        font-size: 28rpx;
        color: rgba(255,255,255,0.9);
      }
    }
  }

  .back-btn {
    position: absolute;
    top: 40rpx;
    left: 30rpx;
    background: rgba(0,0,0,0.3);
    padding: 12rpx 24rpx;
    border-radius: 30rpx;

    text {
      color: #fff;
      font-size: 26rpx;
    }
  }
}

/* 快捷信息 */
.quick-info {
  display: flex;
  align-items: center;
  background: #fff;
  margin: -40rpx 24rpx 24rpx;
  border-radius: 16rpx;
  padding: 30rpx 20rpx;
  position: relative;
  z-index: 10;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.08);

  .info-item {
    flex: 1;
    text-align: center;

    .info-label {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-bottom: 10rpx;
    }

    .info-value {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;

      &.price {
        color: #E54D42;
      }

      &.free {
        color: #39B54A;
      }
    }
  }

  .info-divider {
    width: 1rpx;
    height: 50rpx;
    background: #E8E8E8;
  }
}

/* 详情区域 */
.detail-section {
  padding: 0 24rpx;
}

.section-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;

  .card-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    padding-left: 16rpx;
    border-left: 6rpx solid #4A90D9;
  }

  .card-content {
    font-size: 28rpx;
    color: #666;
    line-height: 1.8;
  }

  .info-list {
    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 16rpx 0;
      border-bottom: 1rpx solid #F5F5F5;

      &:last-child {
        border-bottom: none;
      }

      .row-label {
        font-size: 26rpx;
        color: #999;
        flex-shrink: 0;
        width: 140rpx;
      }

      .row-value {
        font-size: 26rpx;
        color: #333;
        text-align: right;
        flex: 1;
      }
    }
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;

    .tag-item {
      font-size: 26rpx;
      color: #4A90D9;
      background: #E8F2FC;
      padding: 12rpx 24rpx;
      border-radius: 8rpx;
    }
  }
}

.stats-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx 20rpx;

  .stat-item {
    flex: 1;
    text-align: center;

    .stat-value {
      display: block;
      font-size: 36rpx;
      font-weight: 700;
      color: #333;
      margin-bottom: 8rpx;
    }

    .stat-label {
      font-size: 24rpx;
      color: #999;
    }
  }

  .stat-divider {
    width: 1rpx;
    height: 50rpx;
    background: #E8E8E8;
  }
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  background: #fff;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.05);

  .bar-left {
    display: flex;
    gap: 40rpx;

    .bar-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 6rpx;

      .bar-icon {
        width: 44rpx;
        height: 44rpx;
        border-radius: 50%;
        background: #F5F6F8;
        position: relative;

        &.favorite-icon::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 20rpx;
          height: 18rpx;
          background: #999;
          clip-path: polygon(50% 0%, 100% 35%, 80% 100%, 50% 75%, 20% 100%, 0% 35%);
        }

        &.share-icon::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 18rpx;
          height: 18rpx;
          border: 3rpx solid #999;
          border-radius: 50%;
        }
      }

      text {
        font-size: 22rpx;
        color: #666;
      }
    }
  }

  .book-btn {
    flex: 1;
    margin-left: 40rpx;
    background: #4A90D9;
    border-radius: 44rpx;
    padding: 26rpx 0;
    text-align: center;

    text {
      color: #fff;
      font-size: 30rpx;
      font-weight: 500;
    }

    &:active {
      opacity: 0.85;
    }
  }
}

/* 加载状态 */
.loading-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #F5F6F8;

  .loading-spinner {
    width: 60rpx;
    height: 60rpx;
    border: 4rpx solid #E8E8E8;
    border-top-color: #4A90D9;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  .loading-text {
    margin-top: 20rpx;
    font-size: 28rpx;
    color: #999;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
