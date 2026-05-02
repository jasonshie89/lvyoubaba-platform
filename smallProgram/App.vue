<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* 主题色变量 */
$primary-color: #4A90D9;
$primary-light: #E8F2FC;
$danger-color: #E54D42;
$success-color: #39B54A;
$warning-color: #F37B1A;
$text-color: #333333;
$text-secondary: #666666;
$text-placeholder: #999999;
$bg-color: #F5F6F8;
$border-color: #E8E8E8;

/* 全局样式 */
page {
  background-color: $bg-color;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  font-size: 28rpx;
  line-height: 1.6;
  color: $text-color;
  -webkit-font-smoothing: antialiased;
}

/* 通用卡片样式 */
.card {
  background: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

/* 通用按钮 */
.btn-primary {
  background: $primary-color;
  color: #fff;
  border-radius: 44rpx;
  font-size: 30rpx;
  font-weight: 500;
  text-align: center;
  padding: 24rpx 0;
}

.btn-primary:active {
  opacity: 0.85;
}

/* 安全区域 */
.safe-area {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

/* 文字省略 */
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ellipsis-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 分割线 */
.divider {
  height: 1rpx;
  background: $border-color;
  margin: 0 24rpx;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 6rpx 16rpx;
  border-radius: 6rpx;
  font-size: 22rpx;
  font-weight: 500;
}

.status-tag.recruiting {
  background: $primary-light;
  color: $primary-color;
}

.status-tag.ongoing {
  background: #FFF3E0;
  color: #F57C00;
}

.status-tag.completed {
  background: #F5F5F5;
  color: #999999;
}

.status-tag.cancelled {
  background: #FFEBEE;
  color: #E54D42;
}
</style>
