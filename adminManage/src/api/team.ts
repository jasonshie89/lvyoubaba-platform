import request from '../utils/request'

// 分页查询约伴列表
export function getTeamList(params: any) {
  return request({
    url: '/api/team/page',
    method: 'get',
    params
  })
}

// 获取所有约伴
export function getAllTeams() {
  return request({
    url: '/api/team/list',
    method: 'get'
  })
}

// 根据 ID 查询约伴
export function getTeamById(id: number) {
  return request({
    url: '/api/team/' + id,
    method: 'get'
  })
}

// 根据目的地查询约伴
export function getTeamsByDestination(destination: string) {
  return request({
    url: '/api/team/by-destination/' + destination,
    method: 'get'
  })
}

// 获取招募中的约伴
export function getRecruitingTeams() {
  return request({
    url: '/api/team/recruiting',
    method: 'get'
  })
}

// 创建约伴
export function createTeam(data: any) {
  return request({
    url: '/api/team/save',
    method: 'post',
    data
  })
}

// 更新约伴
export function updateTeam(data: any) {
  return request({
    url: '/api/team/update',
    method: 'put',
    data
  })
}

// 删除约伴
export function deleteTeam(id: number) {
  return request({
    url: '/api/team/delete/' + id,
    method: 'delete'
  })
}
