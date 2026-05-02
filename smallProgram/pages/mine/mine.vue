<template>
  <view class="page-container">
    <!-- 用户信息区 -->
    <view class="user-section">
      <view class="user-card">
        <view class="avatar-area" @click="handleAvatarClick">
          <image :src="userInfo.avatarUrl || 'https://picsum.photos/200?random=1'" mode="aspectFill" class="avatar" />
          <view class="avatar-badge" v-if="!userInfo.id">
            <text>登录</text>
          </view>
        </view>
        <view class="user-info">
          <text class="nickname">{{ userInfo.nickname || '点击登录' }}</text>
          <text class="user-desc" v-if="userInfo.id">{{ userInfo.phone || '暂无手机号' }}</text>
          <text class="user-desc" v-else>登录后享受更多服务</text>
        </view>
      </view>

      <!-- 统计数据 -->
      <view class="stats-row">
        <view class="stat-item" @click="goToPage('/pages/myTeam/myTeam')">
          <text class="stat-num">{{ stats.teamCount || 0 }}</text>
          <text class="stat-label">约伴</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item" @click="goToPage('/pages/myActivity/myActivity')">
          <text class="stat-num">{{ stats.activityCount || 0 }}</text>
          <text class="stat-label">动态</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.followers || 0 }}</text>
          <text class="stat-label">粉丝</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.following || 0 }}</text>
          <text class="stat-label">关注</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-card">
        <view class="menu-item" v-for="(item, index) in mainMenus" :key="index" @click="goToPage(item.path)">
          <view class="menu-icon" :style="{background: item.color}">
            <text>{{ item.icon }}</text>
          </view>
          <text class="menu-text">{{ item.name }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 其他功能 -->
    <view class="menu-section">
      <view class="menu-card">
        <view class="menu-item" v-for="(item, index) in otherMenus" :key="index" @click="goToPage(item.path)">
          <view class="menu-icon" :style="{background: item.color}">
            <text>{{ item.icon }}</text>
          </view>
          <text class="menu-text">{{ item.name }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section" v-if="userInfo.id">
      <view class="logout-btn" @click="handleLogout">
        <text>退出登录</text>
      </view>
    </view>

    <!-- 底部安全区 -->
    <view class="safe-bottom"></view>
  </view>
</template>

<script>
import { userApi, teamApi, activityApi } from '../../api/index.js'

export default {
  data() {
    return {
      userInfo: {},
      stats: {
        teamCount: 0,
        activityCount: 0,
        followers: 0,
        following: 0
      },
      mainMenus: [
        { name: '我的约伴', icon: '组队', color: '#E8F2FC', path: '/pages/myTeam/myTeam' },
        { name: '我的动态', icon: '动态', color: '#FFF3E0', path: '/pages/myActivity/myActivity' },
        { name: '我的收藏', icon: '收藏', color: '#FCE4EC', path: '/pages/favorite/favorite' },
        { name: '浏览历史', icon: '历史', color: '#E8F5E9', path: '/pages/history/history' }
      ],
      otherMenus: [
        { name: '帮助中心', icon: '帮助', color: '#F3E5F5', path: '/pages/help/help' },
        { name: '关于我们', icon: '关于', color: '#E0F7FA', path: '/pages/about/about' },
        { name: '设置', icon: '设置', color: '#ECEFF1', path: '/pages/settings/settings' }
      ]
    }
  },
  onLoad() {
    this.loadUserInfo()
  },
  onShow() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        const userId = uni.getStorageSync('userId')
        if (!userId) return

        const user = await userApi.getDetail(userId)
        if (user) {
          this.userInfo = user
        }

        await this.loadStats(userId)
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    },
    async loadStats(userId) {
      try {
        const teams = await teamApi.getPage({ pageNum: 1, pageSize: 100 })
        if (teams && teams.records) {
          this.stats.teamCount = teams.records.filter(t => t.userId === userId).length
        }

        const activities = await activityApi.getByUserId(userId)
        if (activities) {
          this.stats.activityCount = Array.isArray(activities) ? activities.length : 0
        }

        this.stats.followers = 128
        this.stats.following = 36
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    handleAvatarClick() {
      if (!this.userInfo.id) {
        this.handleLogin()
      }
    },
    goToPage(url) {
      if (!this.userInfo.id) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      uni.navigateTo({ url })
    },
    handleLogin() {
      uni.navigateTo({ url: '/pages/login/login' })
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            uni.clearStorageSync()
            this.userInfo = {}
            this.stats = { teamCount: 0, activityCount: 0, followers: 0, following: 0 }
            uni.showToast({ title: '已退出登录', icon: 'success' })
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #F5F6F8;
}

/* 用户信息区 */
.user-section {
  background: #fff;
  padding-bottom: 30rpx;
}

.user-card {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;

  .avatar-area {
    position: relative;
    margin-right: 24rpx;

    .avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
    }

    .avatar-badge {
      position: absolute;
      bottom: 0;
      right: 0;
      background: #4A90D9;
      padding: 6rpx 16rpx;
      border-radius: 20rpx;
      border: 2rpx solid #fff;

      text {
        font-size: 20rpx;
        color: #fff;
      }
    }
  }

  .user-info {
    flex: 1;

    .nickname {
      display: block;
      font-size: 36rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 8rpx;
    }

    .user-desc {
      font-size: 26rpx;
      color: #999;
    }
  }
}

.stats-row {
  display: flex;
  align-items: center;
  margin: 0 30rpx;
  padding: 30rpx 0;
  border-top: 1rpx solid #F0F0F0;

  .stat-item {
    flex: 1;
    text-align: center;

    .stat-num {
      display: block;
      font-size: 40rpx;
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

/* 菜单区域 */
.menu-section {
  margin-top: 24rpx;

  .menu-card {
    background: #fff;
    margin: 0 24rpx;
    border-radius: 16rpx;
    overflow: hidden;

    .menu-item {
      display: flex;
      align-items: center;
      padding: 28rpx 24rpx;
      border-bottom: 1rpx solid #F5F5F5;

      &:last-child {
        border-bottom: none;
      }

      &:active {
        background: #FAFAFA;
      }

      .menu-icon {
        width: 72rpx;
        height: 72rpx;
        border-radius: 14rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20rpx;

        text {
          font-size: 26rpx;
          color: #333;
          font-weight: 500;
        }
      }

      .menu-text {
        flex: 1;
        font-size: 30rpx;
        color: #333;
      }

      .menu-arrow {
        font-size: 36rpx;
        color: #CCC;
      }
    }
  }
}

/* 退出登录 */
.logout-section {
  margin-top: 40rpx;
  padding: 0 24rpx;

  .logout-btn {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx 0;
    text-align: center;

    text {
      color: #E54D42;
      font-size: 30rpx;
    }

    &:active {
      opacity: 0.8;
    }
  }
}

.safe-bottom {
  height: 60rpx;
}
</style>
