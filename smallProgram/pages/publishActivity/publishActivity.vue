<template>
  <view class="page-container">
    <!-- 内容输入区 -->
    <view class="input-section">
      <textarea
        v-model="content"
        placeholder="分享你的旅行故事..."
        maxlength="1000"
        class="content-input"
      />
      <view class="input-footer">
        <text class="char-count">{{ content.length }}/1000</text>
      </view>
    </view>

    <!-- 图片上传区 -->
    <view class="upload-section">
      <view class="section-header">
        <text class="section-title">添加图片</text>
        <text class="section-tip">最多9张</text>
      </view>
      <view class="image-grid">
        <view class="image-item" v-for="(img, idx) in images" :key="idx">
          <image :src="img" mode="aspectFill" class="preview-img" @click="previewImage(idx)" />
          <view class="remove-btn" @click.stop="deleteImage(idx)">
            <text>×</text>
          </view>
        </view>
        <view class="add-image" v-if="images.length < 9" @click="chooseImage">
          <view class="add-icon"></view>
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>

    <!-- 标签区域 -->
    <view class="tag-section">
      <view class="section-header">
        <text class="section-title">添加标签</text>
      </view>
      <input
        v-model="tags"
        placeholder="输入标签，用逗号分隔"
        class="tag-input"
      />
      <view class="hot-tags">
        <text class="hot-label">热门标签</text>
        <view class="tag-list">
          <text class="tag-item" v-for="(tag, idx) in hotTags" :key="idx" @click="addTag(tag)">{{ tag }}</text>
        </view>
      </view>
    </view>

    <!-- 发布按钮 -->
    <view class="submit-section">
      <view class="submit-btn" :class="{ disabled: !canSubmit }" @click="handleSubmit">
        <text>{{ submitting ? '发布中...' : '发布动态' }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { activityApi, fileApi } from '../../api/index.js'

export default {
  data() {
    return {
      content: '',
      images: [],
      tags: '',
      submitting: false,
      hotTags: ['旅行', '美食', '摄影', '自驾', '徒步', '露营']
    }
  },
  computed: {
    canSubmit() {
      return this.content.trim() && !this.submitting
    }
  },
  methods: {
    async chooseImage() {
      const maxCount = 9 - this.images.length
      if (maxCount <= 0) {
        uni.showToast({ title: '最多上传9张图片', icon: 'none' })
        return
      }

      try {
        const res = await new Promise((resolve, reject) => {
          uni.chooseImage({
            count: maxCount,
            sizeType: ['compressed'],
            sourceType: ['album', 'camera'],
            success: resolve,
            fail: reject
          })
        })

        this.images = [...this.images, ...res.tempFilePaths]
      } catch (error) {
        console.log('选择图片取消或失败')
      }
    },

    previewImage(index) {
      uni.previewImage({
        urls: this.images,
        current: index
      })
    },

    deleteImage(index) {
      this.images.splice(index, 1)
    },

    addTag(tag) {
      if (this.tags) {
        if (!this.tags.includes(tag)) {
          this.tags += ',' + tag
        }
      } else {
        this.tags = tag
      }
    },

    async uploadImages() {
      const urls = []
      for (let i = 0; i < this.images.length; i++) {
        const img = this.images[i]
        if (img.startsWith('http://tmp') || img.startsWith('wxfile://') || img.startsWith('file://')) {
          try {
            const url = await fileApi.uploadActivityImage(img)
            urls.push(url)
          } catch (error) {
            console.error('上传图片失败:', error)
            throw new Error('图片上传失败')
          }
        } else {
          urls.push(img)
        }
      }
      return urls
    },

    async handleSubmit() {
      if (!this.canSubmit) return

      this.submitting = true
      try {
        const userId = uni.getStorageSync('userId') || 1

        let imageUrls = []
        if (this.images.length > 0) {
          uni.showLoading({ title: '上传中...' })
          imageUrls = await this.uploadImages()
          uni.hideLoading()
        }

        const activityData = {
          userId: userId,
          content: this.content.trim(),
          images: imageUrls.join(','),
          tags: this.tags
        }

        await activityApi.save(activityData)

        uni.showToast({ title: '发布成功', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (error) {
        console.error('发布失败:', error)
        uni.hideLoading()
        uni.showToast({ title: error.message || '发布失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #F5F6F8;
}

/* 内容输入区 */
.input-section {
  background: #fff;
  padding: 24rpx;
  margin-bottom: 24rpx;

  .content-input {
    width: 100%;
    min-height: 300rpx;
    font-size: 30rpx;
    line-height: 1.8;
    color: #333;
  }

  .input-footer {
    text-align: right;
    padding-top: 16rpx;
    border-top: 1rpx solid #F0F0F0;

    .char-count {
      font-size: 26rpx;
      color: #999;
    }
  }
}

/* 图片上传区 */
.upload-section {
  background: #fff;
  padding: 24rpx;
  margin-bottom: 24rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
    }

    .section-tip {
      font-size: 26rpx;
      color: #999;
    }
  }
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  .image-item {
    position: relative;
    width: 200rpx;
    height: 200rpx;

    .preview-img {
      width: 100%;
      height: 100%;
      border-radius: 12rpx;
    }

    .remove-btn {
      position: absolute;
      top: -12rpx;
      right: -12rpx;
      width: 44rpx;
      height: 44rpx;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      text {
        color: #fff;
        font-size: 32rpx;
      }
    }
  }

  .add-image {
    width: 200rpx;
    height: 200rpx;
    background: #F5F6F8;
    border-radius: 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12rpx;

    .add-icon {
      width: 48rpx;
      height: 48rpx;
      position: relative;

      &::before, &::after {
        content: '';
        position: absolute;
        background: #999;
        border-radius: 2rpx;
      }

      &::before {
        width: 48rpx;
        height: 4rpx;
        top: 50%;
        left: 0;
        transform: translateY(-50%);
      }

      &::after {
        width: 4rpx;
        height: 48rpx;
        left: 50%;
        top: 0;
        transform: translateX(-50%);
      }
    }

    .add-text {
      font-size: 26rpx;
      color: #999;
    }
  }
}

/* 标签区域 */
.tag-section {
  background: #fff;
  padding: 24rpx;
  margin-bottom: 24rpx;

  .section-header {
    margin-bottom: 20rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
    }
  }

  .tag-input {
    width: 100%;
    background: #F5F6F8;
    border-radius: 12rpx;
    padding: 20rpx 24rpx;
    font-size: 28rpx;
    margin-bottom: 20rpx;
  }

  .hot-tags {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16rpx;

    .hot-label {
      font-size: 26rpx;
      color: #999;
      margin-right: 8rpx;
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
    }

    .tag-item {
      font-size: 26rpx;
      color: #4A90D9;
      background: #E8F2FC;
      padding: 12rpx 24rpx;
      border-radius: 8rpx;
    }
  }
}

/* 发布按钮 */
.submit-section {
  padding: 40rpx 24rpx;

  .submit-btn {
    background: #4A90D9;
    border-radius: 50rpx;
    padding: 30rpx 0;
    text-align: center;

    text {
      color: #fff;
      font-size: 32rpx;
      font-weight: 500;
    }

    &.disabled {
      background: #CCC;
    }

    &:active:not(.disabled) {
      opacity: 0.85;
    }
  }
}
</style>
