<template>
  <view class="container">
    <!-- 顶部标题栏 -->
    <!-- <view class="header">
      <view class="back-btn" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="title">发布约伴</text>
      <view class="placeholder"></view>
    </view> -->

    <!-- 封面上传 -->
    <view class="cover-card">
      <view class="cover-upload" @click="chooseCoverImage" v-if="!form.coverImage">
        <view class="upload-icon">
          <text>+</text>
        </view>
        <text class="upload-text">添加封面图片</text>
        <text class="upload-tip">让你的约伴更吸引人</text>
      </view>
      <view class="cover-preview" v-else>
        <image :src="form.coverImage" mode="aspectFill" class="cover-img"></image>
        <view class="cover-delete" @click="deleteCoverImage">
          <text>×</text>
        </view>
      </view>
    </view>

    <!-- 表单内容 -->
    <view class="form-card">
      <view class="form-item">
        <view class="item-label">
          <text>约伴标题</text>
          <text class="required">*</text>
        </view>
        <input type="text" v-model="form.title" placeholder="给你的约伴起个标题吧" maxlength="50" class="input" />
        <text class="word-count">{{ form.title.length }}/50</text>
      </view>

      <view class="form-item">
        <view class="item-label">
          <text>目的地</text>
          <text class="required">*</text>
        </view>
        <input type="text" v-model="form.destination" placeholder="你们要去哪里玩" maxlength="100" class="input" />
      </view>

      <view class="form-row">
        <view class="form-item half">
          <view class="item-label">
            <text>出发日期</text>
            <text class="required">*</text>
          </view>
          <picker mode="date" :value="form.startTime" @change="onDateChange">
            <view class="picker">
              <text :class="form.startTime ? 'text' : 'placeholder'">
                {{ form.startTime || '选择日期' }}
              </text>
              <text class="arrow">›</text>
            </view>
          </picker>
        </view>
        <view class="form-item half">
          <view class="item-label">
            <text>预计天数</text>
          </view>
          <input type="number" v-model="form.duration" placeholder="几天" class="input" />
        </view>
      </view>

      <view class="form-row">
        <view class="form-item half">
          <view class="item-label">
            <text>招募人数</text>
          </view>
          <view class="num-selector">
            <view class="num-btn minus" @click="changeNum(-1)">-</view>
            <text class="num">{{ form.maxMembers || 10 }}</text>
            <view class="num-btn plus" @click="changeNum(1)">+</view>
          </view>
        </view>
        <view class="form-item half">
          <view class="item-label">
            <text>人均预算</text>
          </view>
          <input type="digit" v-model="form.budget" placeholder="多少元" class="input" />
        </view>
      </view>

      <view class="form-item">
        <view class="item-label">
          <text>难度等级</text>
        </view>
        <view class="level-row">
          <view class="level-tag" v-for="item in levels" :key="item.value"
                :class="{ active: form.difficultyLevel === item.value }"
                @click="selectLevel(item.value)">
            {{ item.name }}
          </view>
        </view>
      </view>

      <view class="form-item">
        <view class="item-label">
          <text>联系方式</text>
        </view>
        <input type="text" v-model="form.contactInfo" placeholder="留下你的微信或手机号" maxlength="50" class="input" />
      </view>

      <view class="form-item last">
        <view class="item-label">
          <text>行程说明</text>
          <view class="ai-btn" :class="{ loading: aiGenerating }" @click="generateItinerary" v-if="form.destination && form.duration">
            <text class="ai-icon">✨</text>
            <text>{{ aiGenerating ? '生成中...' : 'AI生成' }}</text>
          </view>
        </view>
        <textarea v-model="form.content" placeholder="简单描述下行程安排、注意事项..." maxlength="500" class="textarea" />
        <text class="word-count">{{ form.content.length }}/500</text>
      </view>
    </view>

    <!-- 底部发布按钮 -->
    <view class="bottom-area">
      <view class="publish-btn" :class="{ disabled: !canSubmit, loading: submitting }" @click="handleSubmit">
        {{ submitting ? '发布中...' : '发布约伴' }}
      </view>
    </view>
  </view>
</template>

<script>
import { teamApi } from '../../api/index.js'
import { fileApi } from '../../api/index.js'
import { aiApi } from '../../api/index.js'

export default {
  data() {
    return {
      form: {
        title: '',
        destination: '',
        startTime: '',
        duration: '',
        maxMembers: 10,
        budget: '',
        difficultyLevel: 2,
        contactInfo: '',
        content: '',
        coverImage: ''
      },
      submitting: false,
      aiGenerating: false,
      levels: [
        { value: 1, name: '轻松' },
        { value: 2, name: '适中' },
        { value: 3, name: '困难' }
      ]
    }
  },
  computed: {
    canSubmit() {
      return this.form.title && this.form.destination && this.form.startTime && !this.submitting
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },

    onDateChange(e) {
      this.form.startTime = e.detail.value
    },

    changeNum(delta) {
      let num = (this.form.maxMembers || 10) + delta
      if (num < 2) num = 2
      if (num > 50) num = 50
      this.form.maxMembers = num
    },

    selectLevel(val) {
      this.form.difficultyLevel = val
    },

    chooseCoverImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          uni.showLoading({ title: '上传中...' })
          try {
            const result = await fileApi.uploadTeamImage(res.tempFilePaths[0])
            this.form.coverImage = result.url
          } catch (e) {
            uni.showToast({ title: '上传失败', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },

    deleteCoverImage() {
      uni.showModal({
        title: '提示',
        content: '确定删除封面吗？',
        success: (res) => {
          if (res.confirm) {
            this.form.coverImage = ''
          }
        }
      })
    },

    async generateItinerary() {
      if (!this.form.destination || !this.form.duration) {
        uni.showToast({ title: '请先填写目的地和天数', icon: 'none' })
        return
      }

      this.aiGenerating = true
      uni.showLoading({ title: 'AI 生成中...' })

      try {
        const result = await aiApi.generateItinerary({
          destination: this.form.destination,
          duration: parseInt(this.form.duration) || 5,
          difficulty: this.form.difficultyLevel,
          maxMembers: this.form.maxMembers,
          budget: this.form.budget || '0'
        })

        if (result.code === 200 && result.data) {
          this.form.content = result.data
          uni.showToast({ title: '生成成功', icon: 'success' })
        } else {
          uni.showToast({ title: result.message || '生成失败', icon: 'none' })
        }
      } catch (e) {
        console.error('AI 生成失败:', e)
        uni.showToast({ title: '生成失败，请重试', icon: 'none' })
      } finally {
        this.aiGenerating = false
        uni.hideLoading()
      }
    },

    async handleSubmit() {
      if (!this.canSubmit) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      this.submitting = true
      try {
        const userId = uni.getStorageSync('userId') || 1
        await teamApi.save({
          userId: userId,
          title: this.form.title,
          destination: this.form.destination,
          startTime: this.form.startTime + ' 08:00:00',
          duration: parseInt(this.form.duration) || 5,
          maxMembers: this.form.maxMembers,
          budget: parseFloat(this.form.budget) || 0,
          difficultyLevel: this.form.difficultyLevel,
          contactInfo: this.form.contactInfo,
          content: this.form.content,
          coverImage: this.form.coverImage,
          status: 1,
          currentMembers: 1,
          isFull: 0
        })
        uni.showToast({ title: '发布成功', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      } catch (e) {
        uni.showToast({ title: '发布失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 140rpx;
}

/* 顶部标题区 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  background: #fff;
}

.back-btn {
  width: 70rpx;
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
}

.back-icon {
  font-size: 40rpx;
  color: #333;
  line-height: 1;
}

.title {
  font-size: 34rpx;
  color: #333;
  font-weight: 600;
}

.placeholder {
  width: 70rpx;
}

/* 封面卡片 */
.cover-card {
  margin: 20rpx 24rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.cover-upload {
  height: 280rpx;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed #d0d3d9;
}

.upload-icon {
  width: 72rpx;
  height: 72rpx;
  background: rgba(7, 193, 96, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.upload-icon text {
  font-size: 40rpx;
  color: #07c160;
  line-height: 1;
}

.upload-text {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.upload-tip {
  font-size: 22rpx;
  color: #999;
}

.cover-preview {
  position: relative;
  height: 280rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.cover-delete {
  position: absolute;
  right: 16rpx;
  bottom: 16rpx;
  width: 60rpx;
  height: 60rpx;
  background: rgba(250, 81, 81, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-delete text {
  font-size: 32rpx;
  color: #fff;
  line-height: 1;
}

/* 表单卡片 */
.form-card {
  margin: 20rpx 24rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 0 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.form-item {
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.form-item.last {
  border-bottom: none;
}

.form-row {
  display: flex;
  border-bottom: 1rpx solid #f0f0f0;
}

.form-row .form-item {
  flex: 1;
  border-bottom: none;
}

.form-row .form-item:first-child {
  padding-right: 24rpx;
}

.form-row .form-item:last-child {
  padding-left: 24rpx;
}

.item-label {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #333;
  justify-content: space-between;
}

.required {
  color: #fa5151;
  margin-left: 6rpx;
}

.ai-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 24rpx;
  padding: 12rpx 20rpx;
  border-radius: 24rpx;
  font-weight: 500;
}

.ai-btn.loading {
  opacity: 0.7;
}

.ai-btn:active:not(.loading) {
  opacity: 0.85;
}

.ai-icon {
  font-size: 26rpx;
}

.input {
  width: 100%;
  height: 76rpx;
  line-height: 76rpx;
  font-size: 28rpx;
  color: #333;
  background: #f7f8fa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
}

.word-count {
  font-size: 22rpx;
  color: #bbb;
  float: right;
  margin-top: 12rpx;
}

/* 日期选择器 */
.picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 76rpx;
  background: #f7f8fa;
  border-radius: 12rpx;
  padding: 0 20rpx;
}

.picker .placeholder {
  font-size: 28rpx;
  color: #bbb;
}

.picker .text {
  font-size: 28rpx;
  color: #333;
}

.picker .arrow {
  font-size: 32rpx;
  color: #ccc;
  font-weight: 500;
}

/* 人数选择器 */
.num-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 76rpx;
  background: #f7f8fa;
  border-radius: 12rpx;
  padding: 0 12rpx;
}

.num-btn {
  width: 52rpx;
  height: 52rpx;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 500;
  line-height: 1;
}

.num-btn.minus {
  background: #fff;
  color: #666;
  border: 1rpx solid #eee;
}

.num-btn.plus {
  background: #07c160;
  color: #fff;
}

.num {
  font-size: 30rpx;
  color: #333;
  font-weight: 600;
  min-width: 60rpx;
  text-align: center;
}

/* 难度选择 */
.level-row {
  display: flex;
  gap: 20rpx;
}

.level-tag {
  flex: 1;
  height: 68rpx;
  line-height: 68rpx;
  text-align: center;
  background: #f7f8fa;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #666;
  transition: all 0.2s;
}

.level-tag.active {
  background: #e8f7ef;
  color: #07c160;
  font-weight: 500;
}

/* 多行文本 */
.textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 16rpx 20rpx;
  background: #f7f8fa;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  box-sizing: border-box;
}

/* 底部发布按钮 */
.bottom-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 24rpx 40rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
}

.publish-btn {
  height: 92rpx;
  line-height: 92rpx;
  text-align: center;
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 46rpx;
  box-shadow: 0 8rpx 20rpx rgba(7, 193, 96, 0.3);
  letter-spacing: 2rpx;
}

.publish-btn.disabled {
  background: #ccc;
  box-shadow: none;
}

.publish-btn.loading {
  opacity: 0.8;
}
</style>
