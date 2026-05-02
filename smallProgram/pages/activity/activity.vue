<template>
  <view class="page-container">
    <scroll-view scroll-y class="activity-scroll" @scrolltolower="loadMore">
      <!-- 动态列表 -->
      <view class="activity-list">
        <view class="activity-card" v-for="(item, index) in activityList" :key="item.id">
          <!-- 用户头像和名称 -->
          <view class="card-header">
            <image :src="'https://picsum.photos/100?random=' + item.userId" mode="aspectFill" class="user-avatar" />
            <view class="user-info">
              <text class="user-name">{{ item.nickname || '旅行者' + item.userId }}</text>
              <text class="post-time">{{ formatTime(item.createTime) }}</text>
            </view>
          </view>

          <!-- 动态内容 -->
          <view class="card-body">
            <text class="content-text">{{ item.content }}</text>

            <!-- 图片展示 -->
            <view class="image-grid" v-if="item.images && parseImages(item.images).length > 0">
              <view
                class="image-item"
                :class="{'single': parseImages(item.images).length === 1, 'double': parseImages(item.images).length === 2}"
                v-for="(img, idx) in parseImages(item.images).slice(0, 9)"
                :key="idx"
                @click="previewImage(parseImages(item.images), idx)"
              >
                <image :src="img" mode="aspectFill" class="preview-img" />
              </view>
            </view>

            <!-- 标签 -->
            <view class="tag-row" v-if="item.tags">
              <text class="tag-item" v-for="(tag, idx) in item.tags.split(',').slice(0, 3)" :key="idx">#{{ tag }}</text>
            </view>
          </view>

          <!-- 互动区域 -->
          <view class="card-footer">
            <view class="action-item" :class="{ liked: item.isLiked }" @click="toggleLike(item)">
              <view class="action-icon like-icon"></view>
              <text class="action-num">{{ item.likeCount || 0 }}</text>
            </view>
            <view class="action-item" @click="handleComment(item)">
              <view class="action-icon comment-icon"></view>
              <text class="action-num">{{ item.commentCount || 0 }}</text>
            </view>
            <view class="action-item" @click="handleShare(item)">
              <view class="action-icon share-icon"></view>
              <text class="action-num">分享</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view class="load-status" v-if="loading || noMore || activityList.length === 0">
        <view class="loading-wrap" v-if="loading">
          <view class="loading-spinner"></view>
          <text>加载中</text>
        </view>
        <text v-else-if="noMore && activityList.length > 0">没有更多了</text>
        <view class="empty-wrap" v-else-if="activityList.length === 0">
          <view class="empty-icon"></view>
          <text class="empty-title">暂无动态</text>
          <text class="empty-desc">快去发布第一条动态吧</text>
        </view>
      </view>
    </scroll-view>

    <!-- 发布按钮 -->
    <view class="publish-btn" @click="goToPublish">
      <view class="btn-plus"></view>
    </view>
  </view>
</template>

<script>
import { activityApi } from '../../api/index.js'

export default {
  data() {
    return {
      activityList: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      noMore: false
    }
  },
  onLoad() {
    this.loadActivityList()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.noMore = false
    this.activityList = []
    this.loadActivityList().then(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    async loadActivityList() {
      if (this.loading || this.noMore) return
      this.loading = true
      try {
        const result = await activityApi.getPage({
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })

        if (result && result.records) {
          if (this.pageNum === 1) {
            this.activityList = result.records
          } else {
            this.activityList = [...this.activityList, ...result.records]
          }
          this.noMore = result.records.length < this.pageSize
        } else {
          this.noMore = true
        }
      } catch (error) {
        console.error('加载失败:', error)
        if (this.pageNum === 1) {
          this.activityList = [
            {id:1,userId:101,nickname:'旅行达人',content:'九寨沟真的太美了！水色五彩斑斓，像仙境一样。',images:'https://picsum.photos/400?random=20',tags:'九寨沟,川西旅行',likeCount:128,commentCount:23,createTime:'2026-04-28 10:30:00'},
            {id:2,userId:102,nickname:'背包客小李',content:'一个人的旅行，在丽江古城遇见最好的自己。漫步在青石板路上，感受古城的宁静。',images:'',tags:'丽江,一个人旅行',likeCount:96,commentCount:15,createTime:'2026-04-27 15:20:00'},
            {id:3,userId:103,nickname:'摄影爱好者',content:'清晨的黄山云海，美到窒息！凌晨4点爬起来，值得！',images:'https://picsum.photos/400?random=21',tags:'黄山,云海,摄影',likeCount:256,commentCount:42,createTime:'2026-04-26 08:45:00'}
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
        this.loadActivityList()
      }
    },
    parseImages(images) {
      if (!images) return []
      if (Array.isArray(images)) return images
      if (typeof images === 'string') {
        try {
          const parsed = JSON.parse(images)
          return Array.isArray(parsed) ? parsed : images.split(',').filter(Boolean)
        } catch {
          return images.split(',').filter(Boolean)
        }
      }
      return []
    },
    formatTime(dateStr) {
      if (!dateStr) return '刚刚'
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
    previewImage(images, index) {
      uni.previewImage({ urls: images, current: index })
    },
    toggleLike(item) {
      item.isLiked = !item.isLiked
      item.likeCount = item.isLiked ? (item.likeCount || 0) + 1 : Math.max(0, (item.likeCount || 1) - 1)
    },
    handleComment(item) {
      uni.showToast({ title: '评论功能开发中', icon: 'none' })
    },
    handleShare(item) {
      uni.showToast({ title: '分享功能开发中', icon: 'none' })
    },
    goToPublish() {
      uni.navigateTo({ url: '/pages/publishActivity/publishActivity' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #F5F6F8;
}

.activity-scroll {
  height: calc(100vh - 100rpx);
  padding: 24rpx;
}

.activity-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 24rpx;

  .user-avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-right: 20rpx;
  }

  .user-info {
    display: flex;
    flex-direction: column;

    .user-name {
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 6rpx;
    }

    .post-time {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.card-body {
  padding: 0 24rpx 24rpx;

  .content-text {
    font-size: 30rpx;
    color: #333;
    line-height: 1.8;
    display: block;
    margin-bottom: 20rpx;
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12rpx;
    margin-bottom: 16rpx;

    .image-item {
      position: relative;
      width: 100%;
      padding-top: 100%;
      border-radius: 12rpx;
      overflow: hidden;

      &.single {
        grid-column: span 2;
        padding-top: 66%;

        .preview-img {
          height: 100%;
        }
      }

      &.double {
        grid-column: span 1;
      }

      .preview-img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
      }
    }
  }

  .tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;

    .tag-item {
      font-size: 24rpx;
      color: #4A90D9;
    }
  }
}

.card-footer {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 24rpx;
  border-top: 1rpx solid #F0F0F0;
  background: #FAFBFC;

  .action-item {
    display: flex;
    align-items: center;
    gap: 10rpx;
    padding: 12rpx 24rpx;

    &:active {
      opacity: 0.7;
    }

    &.liked .like-icon {
      background: #E54D42;
    }

    &.liked .action-num {
      color: #E54D42;
    }

    .action-icon {
      width: 36rpx;
      height: 36rpx;
      border-radius: 50%;
      background: #DDD;
      position: relative;

      &.like-icon::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%) rotate(-45deg);
        width: 14rpx;
        height: 14rpx;
        background: #FFF;
        border-radius: 50% 50% 0 50%;
      }

      &.comment-icon::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 16rpx;
        height: 12rpx;
        border: 3rpx solid #FFF;
        border-radius: 6rpx;
      }

      &.share-icon::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 14rpx;
        height: 14rpx;
        border: 3rpx solid #FFF;
        border-radius: 50%;
      }
    }

    .action-num {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.load-status {
  text-align: center;
  padding: 40rpx 0;

  .loading-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16rpx;

    .loading-spinner {
      width: 40rpx;
      height: 40rpx;
      border: 3rpx solid #E8E8E8;
      border-top-color: #4A90D9;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    text {
      font-size: 26rpx;
      color: #999;
    }
  }

  .empty-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 80rpx 0;

    .empty-icon {
      width: 100rpx;
      height: 100rpx;
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

  text {
    font-size: 26rpx;
    color: #999;
  }
}

.publish-btn {
  position: fixed;
  bottom: 180rpx;
  right: 40rpx;
  width: 100rpx;
  height: 100rpx;
  background: #4A90D9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(74, 144, 217, 0.4);

  &:active {
    transform: scale(0.95);
  }

  .btn-plus {
    width: 36rpx;
    height: 36rpx;
    position: relative;

    &::before, &::after {
      content: '';
      position: absolute;
      background: #fff;
      border-radius: 2rpx;
    }

    &::before {
      width: 36rpx;
      height: 6rpx;
      top: 50%;
      left: 0;
      transform: translateY(-50%);
    }

    &::after {
      width: 6rpx;
      height: 36rpx;
      left: 50%;
      top: 0;
      transform: translateX(-50%);
    }
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
