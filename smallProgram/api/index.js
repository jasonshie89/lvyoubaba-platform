/**
 * API 请求封装模块
 * 统一管理所有 API 请求
 */

// API 基础地址（根据环境切换）
// 开发环境使用本地地址，生产环境使用服务器地址
const BASE_URL = 'http://localhost:8080'
// const BASE_URL = 'http://122.51.82.23:8080' // 生产环境服务器地址

/**
 * 封装请求方法
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          resolve(res.data.data)
        } else {
          uni.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

/**
 * 景点相关 API
 */
export const scenicApi = {
  // 分页查询景点列表
  getPage: (params) => request({
    url: '/api/scenic-spot/page',
    method: 'GET',
    data: params
  }),

  // 获取所有景点
  getList: () => request({
    url: '/api/scenic-spot/list',
    method: 'GET'
  }),

  // 获取景点详情
  getDetail: (id) => request({
    url: '/api/scenic-spot/' + id,
    method: 'GET'
  }),

  // 获取热门景点
  getHot: () => request({
    url: '/api/scenic-spot/hot',
    method: 'GET'
  }),

  // 按城市查询
  getByCity: (city) => request({
    url: '/api/scenic-spot/by-city/' + city,
    method: 'GET'
  })
}

/**
 * 约伴相关 API
 */
export const teamApi = {
  // 分页查询约伴列表
  getPage: (params) => request({
    url: '/api/team/page',
    method: 'GET',
    data: params
  }),

  // 获取所有约伴
  getList: () => request({
    url: '/api/team/list',
    method: 'GET'
  }),

  // 获取约伴详情
  getDetail: (id) => request({
    url: '/api/team/' + id,
    method: 'GET'
  }),

  // 获取招募中的约伴
  getRecruiting: () => request({
    url: '/api/team/recruiting',
    method: 'GET'
  }),

  // 按目的地查询
  getByDestination: (destination) => request({
    url: '/api/team/by-destination/' + destination,
    method: 'GET'
  }),

  // 发布约伴
  save: (data) => request({
    url: '/api/team/save',
    method: 'POST',
    data
  }),

  // 更新约伴
  update: (data) => request({
    url: '/api/team/update',
    method: 'PUT',
    data
  }),

  // 删除约伴
  delete: (id) => request({
    url: '/api/team/delete/' + id,
    method: 'DELETE'
  })
}

/**
 * 动态相关 API
 */
export const activityApi = {
  // 分页查询动态列表
  getPage: (params) => request({
    url: '/api/activity/page',
    method: 'GET',
    data: params
  }),

  // 获取所有动态
  getList: () => request({
    url: '/api/activity/list',
    method: 'GET'
  }),

  // 获取动态详情
  getDetail: (id) => request({
    url: '/api/activity/' + id,
    method: 'GET'
  }),

  // 获取用户发布的动态
  getByUserId: (userId) => request({
    url: '/api/activity/by-user/' + userId,
    method: 'GET'
  }),

  // 获取热门动态
  getHot: () => request({
    url: '/api/activity/hot',
    method: 'GET'
  }),

  // 发布动态
  save: (data) => request({
    url: '/api/activity/save',
    method: 'POST',
    data
  }),

  // 更新动态
  update: (data) => request({
    url: '/api/activity/update',
    method: 'PUT',
    data
  }),

  // 删除动态
  delete: (id) => request({
    url: '/api/activity/delete/' + id,
    method: 'DELETE'
  })
}

/**
 * 用户相关 API
 */
export const userApi = {
  // 分页查询用户列表（管理后台）
  getPage: (params) => request({
    url: '/admin/user/page',
    method: 'GET',
    data: params
  }),

  // 获取所有用户
  getList: () => request({
    url: '/admin/user/list',
    method: 'GET'
  }),

  // 获取用户详情
  getDetail: (id) => request({
    url: '/admin/user/' + id,
    method: 'GET'
  }),

  // 创建用户
  save: (data) => request({
    url: '/admin/user/save',
    method: 'POST',
    data
  }),

  // 更新用户
  update: (data) => request({
    url: '/admin/user/update',
    method: 'PUT',
    data
  }),

  // 删除用户
  delete: (id) => request({
    url: '/admin/user/delete/' + id,
    method: 'DELETE'
  }),

  // 批量删除用户
  batchDelete: (ids) => request({
    url: '/admin/user/batch-delete',
    method: 'DELETE',
    data: ids
  })
}

/**
 * 文件上传相关 API
 */
export const fileApi = {
  // 上传单个图片
  uploadImage: (filePath) => {
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: BASE_URL + '/api/file/upload/image',
        filePath: filePath,
        name: 'file',
        success: (res) => {
          if (res.statusCode === 200) {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data.url)
            } else {
              uni.showToast({ title: data.message || '上传失败', icon: 'none' })
              reject(data)
            }
          } else {
            reject(res)
          }
        },
        fail: (err) => {
          uni.showToast({ title: '上传失败', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  // 上传多张图片
  uploadImages: (filePaths) => {
    return Promise.all(filePaths.map(path => fileApi.uploadImage(path)))
  },

  // 上传头像
  uploadAvatar: (filePath) => {
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: BASE_URL + '/api/file/upload/avatar',
        filePath: filePath,
        name: 'file',
        success: (res) => {
          if (res.statusCode === 200) {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data.url)
            } else {
              uni.showToast({ title: data.message || '上传失败', icon: 'none' })
              reject(data)
            }
          } else {
            reject(res)
          }
        },
        fail: (err) => {
          uni.showToast({ title: '上传失败', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  // 上传动态图片
  uploadActivityImage: (filePath) => {
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: BASE_URL + '/api/file/upload/activity',
        filePath: filePath,
        name: 'file',
        success: (res) => {
          if (res.statusCode === 200) {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data.url)
            } else {
              uni.showToast({ title: data.message || '上传失败', icon: 'none' })
              reject(data)
            }
          } else {
            reject(res)
          }
        },
        fail: (err) => {
          uni.showToast({ title: '上传失败', icon: 'none' })
          reject(err)
        }
      })
    })
  }
}

/**
 * AI 相关 API
 */
export const aiApi = {
  // 生成行程安排
  generateItinerary: (data) => request({
    url: '/api/ai/generate-itinerary',
    method: 'POST',
    data
  }),

  // 提取标签
  extractTags: (data) => request({
    url: '/api/ai/extract-tags',
    method: 'POST',
    data
  }),

  // 内容审核
  checkContent: (data) => request({
    url: '/api/ai/check-content',
    method: 'POST',
    data
  })
}

export default {
  scenicApi,
  teamApi,
  activityApi,
  userApi,
  fileApi,
  aiApi,
  BASE_URL
}