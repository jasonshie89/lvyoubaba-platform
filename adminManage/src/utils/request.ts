import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

/**
 * 文件上传方法
 */
export const uploadFile = (file: File, type: string = 'image'): Promise<any> => {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: `/api/file/upload/${type}`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 30000 // 上传文件超时时间设为30秒
  })
}

export default request
