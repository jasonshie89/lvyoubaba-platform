import request from '../utils/request'

// 分页查询动态列表
export function getActivityList(params: any) {
  return request({
    url: '/api/activity/page',
    method: 'get',
    params
  })
}

// 获取所有动态
export function getAllActivities() {
  return request({
    url: '/api/activity/list',
    method: 'get'
  })
}

// 根据 ID 查询动态
export function getActivityById(id: number) {
  return request({
    url: '/api/activity/' + id,
    method: 'get'
  })
}

// 根据用户ID查询动态
export function getActivitiesByUserId(userId: number) {
  return request({
    url: '/api/activity/by-user/' + userId,
    method: 'get'
  })
}

// 获取热门动态
export function getHotActivities() {
  return request({
    url: '/api/activity/hot',
    method: 'get'
  })
}

// 创建动态
export function createActivity(data: any) {
  return request({
    url: '/api/activity/save',
    method: 'post',
    data
  })
}

// 更新动态
export function updateActivity(data: any) {
  return request({
    url: '/api/activity/update',
    method: 'put',
    data
  })
}

// 删除动态
export function deleteActivity(id: number) {
  return request({
    url: '/api/activity/delete/' + id,
    method: 'delete'
  })
}
