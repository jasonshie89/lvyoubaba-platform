<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">🏔️</div>
        <div class="logo-text">驴友管理平台</div>
      </div>
      <div class="nav-group">
        <div class="nav-title">主菜单</div>
        <el-menu
          :default-active="route.path"
          background-color="transparent"
          text-color="#606266"
          active-text-color="#409eff"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="nav-group">
        <div class="nav-title">数据管理</div>
        <el-menu
          :default-active="route.path"
          background-color="transparent"
          text-color="#606266"
          active-text-color="#409eff"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/user-manage">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/scenic-manage">
            <el-icon><Location /></el-icon>
            <span>景点管理</span>
          </el-menu-item>
          <el-menu-item index="/team-manage">
            <el-icon><UserFilled /></el-icon>
            <span>约伴管理</span>
          </el-menu-item>
          <el-menu-item index="/activity-manage">
            <el-icon><Picture /></el-icon>
            <span>动态管理</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="sidebar-footer">
        <div class="version-info">版本 v1.0.0</div>
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item to="/dashboard">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="5" class="notification-badge">
            <el-button circle :icon="Bell" class="header-btn" />
          </el-badge>
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar" src="https://picsum.photos/seed/admin/100/100" />
              <div class="user-detail">
                <span class="user-name">超级管理员</span>
                <span class="user-role">系统管理员</span>
              </div>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><User /></el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><DataAnalysis /></el-icon>
                  <span>数据统计</span>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Document /></el-icon>
                  <span>操作日志</span>
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Odometer,
  User,
  Location,
  UserFilled,
  Picture,
  Bell,
  ArrowDown,
  Lock,
  DataAnalysis,
  Document,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()

const currentRoute = computed(() => {
  const routeMap: Record<string, string> = {
    '/dashboard': '仪表盘',
    '/user-manage': '用户管理',
    '/scenic-manage': '景点管理',
    '/team-manage': '约伴管理',
    '/activity-manage': '动态管理'
  }
  return routeMap[route.path] || ''
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  width: 220px !important;
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid #e4e7ed;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
}

.nav-group {
  padding: 16px 0 8px;
}

.nav-title {
  font-size: 12px;
  color: #909399;
  padding: 0 20px 8px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.sidebar-menu {
  border: none;
  padding: 0 12px;
}

.sidebar-menu .el-menu-item {
  height: 44px !important;
  line-height: 44px !important;
  border-radius: 6px !important;
  margin-bottom: 4px !important;
  padding: 0 16px !important;
  transition: all 0.2s ease;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
  margin-top: auto;
}

.version-info {
  font-size: 12px;
  color: #c0c4cc;
  text-align: center;
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.header-left {
  flex: 1;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-btn {
  width: 36px;
  height: 36px;
  background: #f5f7fa;
  border: none;
  color: #606266;
  transition: all 0.2s ease;
}

.header-btn:hover {
  background: #ecf5ff;
  color: #409eff;
}

.notification-badge :deep(.el-badge__content) {
  background: #f56c6c;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: #ecf5ff;
  color: #409eff;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.arrow-icon {
  font-size: 12px;
  color: #c0c4cc;
  margin-left: 4px;
}

.main-content {
  background: #f5f7fa;
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

:deep(.el-dropdown-menu) {
  padding: 6px 0;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

:deep(.el-dropdown-menu__item) {
  padding: 0 16px;
  font-size: 14px;
  height: 40px;
  line-height: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #ecf5ff;
  color: #409eff;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}
</style>