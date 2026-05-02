<template>
  <view class="page-container">
    <!-- 搜索栏 -->
    <view class="search-header">
      <view class="search-box" @click="handleSearch">
        <view class="search-icon"></view>
        <text class="search-text">搜索景点、约伴、动态</text>
      </view>
    </view>

    <!-- 轮播图 -->
    <view class="banner-wrap">
      <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="4000" :duration="500" circular indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#ffffff">
        <swiper-item v-for="(item, index) in banners" :key="index">
          <image :src="item.image" mode="aspectFill" class="banner-img" />
          <view class="banner-title">{{ item.title }}</view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 分类入口 -->
    <view class="category-section">
      <view class="category-item" v-for="(item, index) in categories" :key="index" @click="handleCategory(item.type)">
        <view class="category-icon" :style="{background: item.bgColor}">
          <text>{{ item.icon }}</text>
        </view>
        <text class="category-name">{{ item.name }}</text>
      </view>
    </view>

    <!-- 热门景点 -->
    <view class="section-wrap">
      <view class="section-header">
        <text class="section-title">热门景点</text>
        <view class="section-more" @click="goToScenicList">
          <text>更多</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      <scroll-view scroll-x class="scenic-scroll" :show-scrollbar="false">
        <view class="scenic-list">
          <view class="scenic-card" v-for="(item, index) in hotSpots" :key="item.id" @click="goToScenicDetail(item)">
            <image :src="item.coverImage || 'https://picsum.photos/400/300?random=' + item.id" mode="aspectFill" class="scenic-img" />
            <view class="scenic-info">
              <text class="scenic-name">{{ item.name }}</text>
              <text class="scenic-location">{{ item.province }} · {{ item.city }}</text>
              <view class="scenic-bottom">
                <text class="scenic-price" v-if="item.ticketPrice > 0">¥{{ item.ticketPrice }}</text>
                <text class="scenic-free" v-else>免费</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 最新约伴 -->
    <view class="section-wrap">
      <view class="section-header">
        <text class="section-title">最新约伴</text>
        <view class="section-more" @click="goToTeamList">
          <text>更多</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      <view class="team-list">
        <view class="team-card" v-for="(item, index) in teams" :key="item.id" @click="goToTeamDetail(item)">
          <view class="team-main">
            <view class="team-top">
              <text class="team-title">{{ item.title }}</text>
              <view class="team-status" :class="'status-' + (item.status || 1)">{{ statusMap[item.status || 1] }}</view>
            </view>
            <view class="team-info">
              <text class="info-item">{{ item.destination }}</text>
              <text class="info-item">{{ formatDate(item.startTime) }}</text>
            </view>
            <view class="team-meta">
              <text class="team-budget">¥{{ item.budget || 0 }}/人</text>
              <text class="team-members">{{ item.currentMembers || 1 }}/{{ item.maxMembers || 10 }}人</text>
            </view>
          </view>
          <text class="team-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 底部安全区 -->
    <view class="safe-area"></view>
  </view>
</template>

<script>
import { scenicApi, teamApi } from '../../api/index.js'

export default {
  data() {
    return {
      banners: [
        { image: 'https://picsum.photos/750/400?random=101', title: '探索未知的风景' },
        { image: 'https://picsum.photos/750/400?random=102', title: '结识志同道合的旅友' },
        { image: 'https://picsum.photos/750/400?random=103', title: '分享你的旅行故事' }
      ],
      categories: [
        { name: '山川', type: 'mountain', icon: '山', bgColor: '#E8F5E9' },
        { name: '湖泊', type: 'lake', icon: '湖', bgColor: '#E3F2FD' },
        { name: '古城', type: 'ancient', icon: '城', bgColor: '#FFF3E0' },
        { name: '海岛', type: 'island', icon: '岛', bgColor: '#FCE4EC' }
      ],
      hotSpots: [],
      teams: [],
      statusMap: { 1: '招募中', 2: '进行中', 3: '已完成', 0: '已取消' }
    }
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.loadData().then(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    async loadData() {
      try {
        const [hotSpots, teams] = await Promise.all([
          scenicApi.getHot(),
          teamApi.getRecruiting()
        ])
        this.hotSpots = hotSpots || []
        this.teams = teams || []
      } catch (error) {
        console.error('加载数据失败:', error)
        this.loadFallbackData()
      }
    },
    loadFallbackData() {
      this.hotSpots = [
        {id:1,name:'九寨沟',province:'四川',city:'阿坝',coverImage:'https://picsum.photos/400/300?random=11',ticketPrice:169,viewCount:28560},
        {id:2,name:'黄山',province:'安徽',city:'黄山',coverImage:'https://picsum.photos/400/300?random=12',ticketPrice:190,viewCount:32100},
        {id:3,name:'西湖',province:'浙江',city:'杭州',coverImage:'https://picsum.photos/400/300?random=13',ticketPrice:0,viewCount:58960},
        {id:4,name:'丽江古城',province:'云南',city:'丽江',coverImage:'https://picsum.photos/400/300?random=14',ticketPrice:50,viewCount:35230}
      ]
      this.teams = [
        {id:1,title:'川西小环线5日游',destination:'四川·稻城亚丁',startTime:'2026-05-15',budget:2800,status:1,currentMembers:2,maxMembers:6},
        {id:2,title:'云南大理丽江游',destination:'云南·丽江',startTime:'2026-05-20',budget:3200,status:1,currentMembers:4,maxMembers:8},
        {id:3,title:'西藏拉萨朝圣之旅',destination:'西藏·拉萨',startTime:'2026-06-01',budget:5800,status:1,currentMembers:2,maxMembers:5}
      ]
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.split(' ')[0] || dateStr
    },
    handleSearch() {
      uni.showToast({ title: '搜索功能开发中', icon: 'none' })
    },
    handleCategory(type) {
      uni.showToast({ title: '分类筛选开发中', icon: 'none' })
    },
    goToScenicList() {
      uni.showToast({ title: '更多景点', icon: 'none' })
    },
    goToScenicDetail(item) {
      uni.navigateTo({ url: '/pages/scenicDetail/scenicDetail?id=' + item.id })
    },
    goToTeamList() {
      uni.switchTab({ url: '/pages/team/team' })
    },
    goToTeamDetail(item) {
      uni.navigateTo({ url: '/pages/teamDetail/teamDetail?id=' + item.id })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #F5F6F8;
}

/* 搜索栏 */
.search-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  padding: 24rpx 30rpx;

  .search-box {
    display: flex;
    align-items: center;
    background: #F5F6F8;
    border-radius: 44rpx;
    padding: 20rpx 30rpx;

    .search-icon {
      width: 32rpx;
      height: 32rpx;
      border: 3rpx solid #999;
      border-radius: 50%;
      margin-right: 16rpx;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        right: -6rpx;
        bottom: -2rpx;
        width: 10rpx;
        height: 3rpx;
        background: #999;
        transform: rotate(45deg);
      }
    }

    .search-text {
      font-size: 28rpx;
      color: #999;
    }
  }
}

/* 轮播图 */
.banner-wrap {
  margin: 24rpx;
  border-radius: 20rpx;
  overflow: hidden;
  position: relative;

  .banner-swiper {
    height: 320rpx;
  }

  .banner-img {
    width: 100%;
    height: 100%;
  }

  .banner-title {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 50rpx 24rpx 20rpx;
    background: linear-gradient(transparent, rgba(0,0,0,0.5));
    color: #fff;
    font-size: 32rpx;
    font-weight: 500;
  }
}

/* 分类入口 */
.category-section {
  display: flex;
  justify-content: space-around;
  background: #fff;
  margin: 0 24rpx 24rpx;
  padding: 30rpx 0;
  border-radius: 16rpx;

  .category-item {
    display: flex;
    flex-direction: column;
    align-items: center;

    .category-icon {
      width: 88rpx;
      height: 88rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12rpx;

      text {
        font-size: 32rpx;
        color: #333;
        font-weight: 500;
      }
    }

    .category-name {
      font-size: 26rpx;
      color: #333;
    }
  }
}

/* 通用section */
.section-wrap {
  background: #fff;
  margin: 0 24rpx 24rpx;
  padding: 28rpx;
  border-radius: 16rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }

    .section-more {
      display: flex;
      align-items: center;

      text {
        font-size: 26rpx;
        color: #999;
      }

      .more-arrow {
        font-size: 32rpx;
        margin-left: 4rpx;
        color: #CCC;
      }
    }
  }
}

/* 景点列表 */
.scenic-scroll {
  width: 100%;
  white-space: nowrap;
}

.scenic-list {
  display: inline-flex;
  gap: 20rpx;
}

.scenic-card {
  width: 260rpx;
  flex-shrink: 0;
  border-radius: 12rpx;
  overflow: hidden;
  background: #F8F9FA;

  .scenic-img {
    width: 100%;
    height: 180rpx;
  }

  .scenic-info {
    padding: 16rpx;

    .scenic-name {
      display: block;
      font-size: 28rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 8rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .scenic-location {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-bottom: 8rpx;
    }

    .scenic-bottom {
      .scenic-price {
        font-size: 28rpx;
        font-weight: 600;
        color: #E54D42;
      }

      .scenic-free {
        font-size: 26rpx;
        color: #39B54A;
      }
    }
  }
}

/* 约伴列表 */
.team-list {
  .team-card {
    display: flex;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #F0F0F0;

    &:last-child {
      border-bottom: none;
    }

    .team-main {
      flex: 1;
      overflow: hidden;
    }

    .team-top {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      .team-title {
        font-size: 30rpx;
        font-weight: 600;
        color: #333;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-right: 16rpx;
      }

      .team-status {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 6rpx;
        flex-shrink: 0;

        &.status-1 {
          background: #E8F2FC;
          color: #4A90D9;
        }

        &.status-2 {
          background: #FFF3E0;
          color: #F57C00;
        }

        &.status-3 {
          background: #F5F5F5;
          color: #999;
        }

        &.status-0 {
          background: #FFEBEE;
          color: #E54D42;
        }
      }
    }

    .team-info {
      display: flex;
      gap: 24rpx;
      margin-bottom: 12rpx;

      .info-item {
        font-size: 26rpx;
        color: #666;
      }
    }

    .team-meta {
      display: flex;
      gap: 24rpx;

      .team-budget {
        font-size: 28rpx;
        font-weight: 600;
        color: #E54D42;
      }

      .team-members {
        font-size: 26rpx;
        color: #999;
      }
    }

    .team-arrow {
      font-size: 36rpx;
      color: #CCC;
      margin-left: 16rpx;
    }
  }
}

.safe-area {
  height: 40rpx;
}
</style>
