<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">数据概览</h1>
      <p class="page-subtitle">欢迎回来，管理员</p>
    </div>

    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ background: stat.bgColor, color: stat.iconColor }">
            <el-icon :size="24"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
            <el-icon><component :is="stat.trend > 0 ? 'Top' : 'Bottom'" /></el-icon>
            <span>{{ Math.abs(stat.trend) }}%</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="shortcuts">
            <div
              v-for="item in shortcuts"
              :key="item.path"
              class="shortcut-item"
              @click="$router.push(item.path)"
            >
              <div class="shortcut-icon" :style="{ background: item.bgColor, color: item.iconColor }">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <span class="shortcut-name">{{ item.name }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="system-info">
            <div class="info-item" v-for="info in systemInfo" :key="info.label">
              <span class="info-label">{{ info.label }}</span>
              <span class="info-value">{{ info.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热门景点</span>
            </div>
          </template>
          <div class="ranking-list">
            <div class="ranking-item" v-for="(item, index) in hotScenicSpots" :key="index">
              <span class="rank-num" :class="index < 3 ? 'top' : ''">{{ index + 1 }}</span>
              <div class="rank-content">
                <div class="rank-name">{{ item.name }}</div>
                <div class="rank-location">{{ item.location }}</div>
              </div>
              <span class="rank-views">{{ item.views }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="content-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新约伴</span>
            </div>
          </template>
          <div class="team-list">
            <div class="team-item" v-for="item in recentTeams" :key="item.title">
              <el-avatar :size="40" class="team-avatar" />
              <div class="team-content">
                <div class="team-title">{{ item.title }}</div>
                <div class="team-meta">{{ item.destination }} · {{ item.members }}人</div>
              </div>
              <el-tag :type="item.status === '招募中' ? 'success' : 'info'" size="small">
                {{ item.status }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { User, Shop, UserFilled, ChatDotRound, Top, Bottom, DataLine, Avatar } from '@element-plus/icons-vue'

const stats = [
  { label: '用户总数', value: '1,234', icon: Avatar, trend: 12.5, bgColor: '#ecf5ff', iconColor: '#409eff' },
  { label: '景点数量', value: '567', icon: Shop, trend: 8.3, bgColor: '#fdf6ec', iconColor: '#e6a23c' },
  { label: '约伴数量', value: '890', icon: UserFilled, trend: 15.2, bgColor: '#f0f9eb', iconColor: '#67c23a' },
  { label: '动态数量', value: '2,345', icon: ChatDotRound, trend: -2.1, bgColor: '#fef0f6', iconColor: '#f56c6c' }
]

const shortcuts = [
  { name: '用户管理', path: '/user-manage', icon: Avatar, bgColor: '#ecf5ff', iconColor: '#409eff' },
  { name: '景点管理', path: '/scenic-manage', icon: Shop, bgColor: '#fdf6ec', iconColor: '#e6a23c' },
  { name: '约伴管理', path: '/team-manage', icon: UserFilled, bgColor: '#f0f9eb', iconColor: '#67c23a' },
  { name: '动态管理', path: '/activity-manage', icon: ChatDotRound, bgColor: '#fef0f6', iconColor: '#f56c6c' }
]

const systemInfo = [
  { label: '系统版本', value: 'v1.0.0' },
  { label: '后端地址', value: 'localhost:8080' },
  { label: '数据库', value: 'MySQL 5.7' },
  { label: '运行环境', value: '开发环境' }
]

const hotScenicSpots = [
  { name: '九寨沟', location: '四川·阿坝', views: '12.5k' },
  { name: '丽江古城', location: '云南·丽江', views: '10.2k' },
  { name: '西湖', location: '浙江·杭州', views: '9.8k' },
  { name: '黄山', location: '安徽·黄山', views: '8.6k' },
  { name: '稻城亚丁', location: '四川·甘孜', views: '7.3k' }
]

const recentTeams = [
  { title: '川西小环线5日游', destination: '稻城亚丁', members: 4, status: '招募中' },
  { title: '云南大理丽江游', destination: '丽江', members: 6, status: '已满员' },
  { title: '西藏拉萨朝圣', destination: '拉萨', members: 5, status: '招募中' }
]
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.stat-cards {
  margin-bottom: 4px;
}

.stat-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.2s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.1);
  border-color: #c0c4cc;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
}

.stat-trend.up {
  color: #67c23a;
  background: #f0f9eb;
}

.stat-trend.down {
  color: #f56c6c;
  background: #fef0f0;
}

.content-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.2s ease;
}

.content-card:hover {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.08);
}

.card-header {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.shortcuts {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.shortcut-item:hover {
  background: #ecf5ff;
  border-color: #b3d8ff;
}

.shortcut-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.shortcut-name {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.info-value {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: background 0.2s;
}

.ranking-item:hover {
  background: #f5f7fa;
}

.rank-num {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #909399;
  flex-shrink: 0;
}

.rank-num.top {
  background: #409eff;
  color: #fff;
}

.rank-content {
  flex: 1;
}

.rank-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.rank-location {
  font-size: 12px;
  color: #909399;
}

.rank-views {
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
}

.team-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.team-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: background 0.2s;
}

.team-item:hover {
  background: #f5f7fa;
}

.team-avatar {
  flex-shrink: 0;
}

.team-content {
  flex: 1;
  min-width: 0;
}

.team-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.team-meta {
  font-size: 12px;
  color: #909399;
}
</style>