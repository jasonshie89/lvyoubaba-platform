<template>
  <view class="page-container">
    <!-- 顶部搜索 -->
    <view class="header-section">
      <view class="search-row">
        <view class="search-input">
          <input type="text" placeholder="搜索目的地" v-model="keyword" @confirm="handleSearch" />
        </view>
        <view class="filter-btn" :class="{ active: hasFilter }" @click="toggleFilter">
          <text>筛选</text>
        </view>
      </view>

      <!-- 筛选面板 -->
      <view class="filter-panel" v-if="showFilter">
        <view class="filter-row">
          <text class="filter-label">活动状态</text>
          <view class="filter-options">
            <text class="option-item" :class="{ active: filterStatus === null }" @click="filterStatus = null">全部</text>
            <text class="option-item" :class="{ active: filterStatus === 1 }" @click="filterStatus = 1">招募中</text>
            <text class="option-item" :class="{ active: filterStatus === 2 }" @click="filterStatus = 2">进行中</text>
          </view>
        </view>
        <view class="filter-actions">
          <text class="reset-btn" @click="resetFilter">重置</text>
          <text class="confirm-btn" @click="confirmFilter">确定</text>
        </view>
      </view>
    </view>

    <!-- 约伴列表 -->
    <scroll-view scroll-y class="list-scroll" @scrolltolower="loadMore">
      <view class="team-list">
        <view class="team-card" v-for="(item, index) in teamList" :key="item.id" @click="goToDetail(item)">
          <!-- 卡片头部 -->
          <view class="card-header">
            <view class="header-left">
              <text class="team-title">{{ item.title }}</text>
              <view class="team-status" :class="'status-' + (item.status || 1)">
                {{ statusMap[item.status || 1] }}
              </view>
            </view>
            <text class="team-date">{{ formatDate(item.startTime) }}</text>
          </view>

          <!-- 卡片内容 -->
          <view class="card-body">
            <view class="info-grid">
              <view class="info-col">
                <text class="info-label">目的地</text>
                <text class="info-value">{{ item.destination }}</text>
              </view>
              <view class="info-col">
                <text class="info-label">行程天数</text>
                <text class="info-value">{{ item.duration || 5 }}天</text>
              </view>
            </view>
            <view class="info-grid">
              <view class="info-col">
                <text class="info-label">人均预算</text>
                <text class="info-value highlight">¥{{ item.budget || 0 }}</text>
              </view>
              <view class="info-col">
                <text class="info-label">人数</text>
                <text class="info-value">{{ item.currentMembers || 1 }}/{{ item.maxMembers || 10 }}人</text>
              </view>
            </view>
          </view>

          <!-- 卡片底部 -->
          <view class="card-footer">
            <view class="user-row">
              <image :src="'https://picsum.photos/60?random=' + item.userId" class="user-avatar" />
              <text class="user-name">用户{{ item.userId }}</text>
              <text class="publish-time">{{ formatCreateTime(item.createTime) }}</text>
            </view>
            <view class="action-hint">
              <text>查看详情</text>
              <text class="arrow">›</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="!loading && teamList.length === 0">
        <view class="empty-icon"></view>
        <text class="empty-title">暂无约伴</text>
        <text class="empty-desc">快去发布第一条约伴吧</text>
      </view>

      <!-- 加载状态 -->
      <view class="load-more" v-if="loading">
        <view class="loading-icon"></view>
        <text>加载中</text>
      </view>
      <view class="no-more" v-else-if="noMore && teamList.length > 0">
        <text>没有更多了</text>
      </view>
    </scroll-view>

    <!-- 发布按钮 -->
    <view class="publish-btn" @click="goToPublish">
      <view class="btn-icon"></view>
      <text class="btn-text">发布约伴</text>
    </view>
  </view>
</template>

<script>
import { teamApi } from '../../api/index.js'

export default {
  data() {
    return {
      keyword: '',
      showFilter: false,
      filterStatus: null,
      hasFilter: false,
      teamList: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      noMore: false,
      statusMap: { 1: '招募中', 2: '进行中', 3: '已完成', 0: '已取消' }
    }
  },
  onLoad() {
    this.loadTeamList()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.noMore = false
    this.teamList = []
    this.loadTeamList().then(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    async loadTeamList() {
      if (this.loading || this.noMore) return
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.keyword) params.keyword = this.keyword
        if (this.filterStatus !== null) params.status = this.filterStatus

        const result = await teamApi.getPage(params)

        if (result && result.records) {
          if (this.pageNum === 1) {
            this.teamList = result.records
          } else {
            this.teamList = [...this.teamList, ...result.records]
          }
          this.noMore = result.records.length < this.pageSize
        } else {
          this.noMore = true
        }
      } catch (error) {
        console.error('加载失败:', error)
        if (this.pageNum === 1) {
          this.teamList = [
            {id:1,title:'川西小环线5日游',destination:'四川·稻城亚丁',startTime:'2026-05-15',duration:5,budget:2800,status:1,currentMembers:2,maxMembers:6,userId:1,createTime:'2026-04-01 10:00:00'},
            {id:2,title:'云南大理丽江6日游',destination:'云南·丽江',startTime:'2026-05-20',duration:6,budget:3200,status:1,currentMembers:4,maxMembers:8,userId:2,createTime:'2026-04-02 14:30:00'},
            {id:3,title:'西藏拉萨朝圣之旅',destination:'西藏·拉萨',startTime:'2026-06-01',duration:8,budget:5800,status:1,currentMembers:2,maxMembers:5,userId:3,createTime:'2026-04-03 09:15:00'}
          ]
        }
        this.noMore = true
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (!this.noMore) {
        this.pageNum++
        this.loadTeamList()
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.noMore = false
      this.teamList = []
      this.loadTeamList()
    },
    toggleFilter() {
      this.showFilter = !this.showFilter
    },
    resetFilter() {
      this.filterStatus = null
      this.hasFilter = false
      this.showFilter = false
      this.handleSearch()
    },
    confirmFilter() {
      this.hasFilter = this.filterStatus !== null
      this.showFilter = false
      this.handleSearch()
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.split(' ')[0] || dateStr
    },
    formatCreateTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 60) return minutes + '分钟前'
      if (hours < 24) return hours + '小时前'
      if (days < 7) return days + '天前'
      return dateStr.split(' ')[0]
    },
    goToDetail(item) {
      uni.navigateTo({ url: '/pages/teamDetail/teamDetail?id=' + item.id })
    },
    goToPublish() {
      uni.navigateTo({ url: '/pages/publishTeam/publishTeam' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #F5F6F8;
}

/* 头部搜索 */
.header-section {
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;

  .search-row {
    display: flex;
    align-items: center;
    padding: 24rpx;
    gap: 20rpx;

    .search-input {
      flex: 1;
      background: #F5F6F8;
      border-radius: 44rpx;
      padding: 0 30rpx;

      input {
        height: 80rpx;
        font-size: 28rpx;
      }
    }

    .filter-btn {
      padding: 20rpx 32rpx;
      background: #F5F6F8;
      border-radius: 44rpx;

      text {
        font-size: 28rpx;
        color: #666;
      }

      &.active {
        background: #4A90D9;

        text {
          color: #fff;
        }
      }
    }
  }

  .filter-panel {
    padding: 24rpx;
    border-top: 1rpx solid #F0F0F0;

    .filter-row {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;

      .filter-label {
        font-size: 26rpx;
        color: #666;
        width: 120rpx;
      }

      .filter-options {
        display: flex;
        gap: 16rpx;

        .option-item {
          font-size: 26rpx;
          color: #666;
          padding: 12rpx 24rpx;
          background: #F5F6F8;
          border-radius: 8rpx;

          &.active {
            color: #fff;
            background: #4A90D9;
          }
        }
      }
    }

    .filter-actions {
      display: flex;
      justify-content: flex-end;
      gap: 24rpx;
      margin-top: 20rpx;

      .reset-btn, .confirm-btn {
        font-size: 28rpx;
        padding: 16rpx 40rpx;
        border-radius: 8rpx;
      }

      .reset-btn {
        color: #666;
        background: #F5F6F8;
      }

      .confirm-btn {
        color: #fff;
        background: #4A90D9;
      }
    }
  }
}

/* 列表区域 */
.list-scroll {
  height: calc(100vh - 130rpx);
  padding: 24rpx;
}

.team-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 24rpx;
    border-bottom: 1rpx solid #F5F5F5;

    .header-left {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 16rpx;

      .team-title {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
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

    .team-date {
      font-size: 26rpx;
      color: #999;
      flex-shrink: 0;
      margin-left: 16rpx;
    }
  }

  .card-body {
    padding: 20rpx 24rpx;

    .info-grid {
      display: flex;
      margin-bottom: 16rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .info-col {
        flex: 1;

        .info-label {
          display: block;
          font-size: 24rpx;
          color: #999;
          margin-bottom: 8rpx;
        }

        .info-value {
          font-size: 28rpx;
          color: #333;

          &.highlight {
            color: #E54D42;
            font-weight: 600;
          }
        }
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 24rpx;
    background: #FAFBFC;
    border-top: 1rpx solid #F5F5F5;

    .user-row {
      display: flex;
      align-items: center;
      gap: 12rpx;

      .user-avatar {
        width: 48rpx;
        height: 48rpx;
        border-radius: 50%;
      }

      .user-name {
        font-size: 26rpx;
        color: #666;
      }

      .publish-time {
        font-size: 24rpx;
        color: #999;
        margin-left: 16rpx;
      }
    }

    .action-hint {
      display: flex;
      align-items: center;

      text {
        font-size: 26rpx;
        color: #4A90D9;
      }

      .arrow {
        font-size: 32rpx;
        margin-left: 4rpx;
      }
    }
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;

  .empty-icon {
    width: 120rpx;
    height: 120rpx;
    background: #E8E8E8;
    border-radius: 50%;
    margin-bottom: 24rpx;
  }

  .empty-title {
    font-size: 32rpx;
    color: #333;
    margin-bottom: 12rpx;
  }

  .empty-desc {
    font-size: 26rpx;
    color: #999;
  }
}

/* 加载状态 */
.load-more, .no-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
  font-size: 26rpx;
  color: #999;
  gap: 12rpx;

  .loading-icon {
    width: 32rpx;
    height: 32rpx;
    border: 3rpx solid #E8E8E8;
    border-top-color: #4A90D9;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
}

/* 发布按钮 */
.publish-btn {
  position: fixed;
  bottom: 180rpx;
  right: 30rpx;
  display: flex;
  align-items: center;
  background: #4A90D9;
  padding: 20rpx 36rpx;
  border-radius: 50rpx;
  box-shadow: 0 8rpx 24rpx rgba(74, 144, 217, 0.4);

  &:active {
    transform: scale(0.95);
  }

  .btn-icon {
    width: 32rpx;
    height: 32rpx;
    position: relative;
    margin-right: 12rpx;

    &::before, &::after {
      content: '';
      position: absolute;
      background: #fff;
      border-radius: 2rpx;
    }

    &::before {
      width: 32rpx;
      height: 4rpx;
      top: 50%;
      left: 0;
      transform: translateY(-50%);
    }

    &::after {
      width: 4rpx;
      height: 32rpx;
      left: 50%;
      top: 0;
      transform: translateX(-50%);
    }
  }

  .btn-text {
    font-size: 28rpx;
    color: #fff;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
