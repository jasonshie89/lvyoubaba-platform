import request from '../utils/request'

// 分页查询用户列表
export function getUserList(params: any) {
  return request({
    url: '/admin/user/page',
    method: 'get',
    params
  })
}

// 获取所有用户
export function getAllUsers() {
  return request({
    url: '/admin/user/list',
    method: 'get'
  })
}

// 根据 ID 查询用户
export function getUserById(id: number) {
  return request({
    url: '/admin/user/' + id,
    method: 'get'
  })
}

// 创建用户
export function createUser(data: any) {
  return request({
    url: '/admin/user/save',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data: any) {
  return request({
    url: '/admin/user/update',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id: number) {
  return request({
    url: '/admin/user/delete/' + id,
    method: 'delete'
  })
}

// 批量删除用户
export function batchDeleteUsers(ids: number[]) {
  return request({
    url: '/admin/user/batch-delete',
    method: 'delete',
    data: ids
  })
}
