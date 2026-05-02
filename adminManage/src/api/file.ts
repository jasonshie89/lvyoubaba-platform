import request, { uploadFile } from '../utils/request'

// 上传图片
export function uploadImage(file: File) {
  return uploadFile(file, 'image')
}

// 上传头像
export function uploadAvatar(file: File) {
  return uploadFile(file, 'avatar')
}

// 上传景点图片
export function uploadScenicImage(file: File) {
  return uploadFile(file, 'scenic')
}

// 上传动态图片
export function uploadActivityImage(file: File) {
  return uploadFile(file, 'activity')
}

// 删除文件
export function deleteFile(url: string) {
  return request({
    url: '/api/file/delete',
    method: 'delete',
    params: { url }
  })
}