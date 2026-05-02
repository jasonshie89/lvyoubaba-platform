import request from '../utils/request'

// 分页查询景点列表
export function getScenicSpotList(params: any) {
  return request({
    url: '/api/scenic-spot/page',
    method: 'get',
    params
  })
}

// 获取所有景点
export function getAllScenicSpots() {
  return request({
    url: '/api/scenic-spot/list',
    method: 'get'
  })
}

// 根据 ID 查询景点
export function getScenicSpotById(id: number) {
  return request({
    url: '/api/scenic-spot/' + id,
    method: 'get'
  })
}

// 根据城市查询景点
export function getScenicSpotsByCity(city: string) {
  return request({
    url: '/api/scenic-spot/by-city/' + city,
    method: 'get'
  })
}

// 获取热门景点
export function getHotScenicSpots() {
  return request({
    url: '/api/scenic-spot/hot',
    method: 'get'
  })
}

// 创建景点
export function createScenicSpot(data: any) {
  return request({
    url: '/api/scenic-spot/save',
    method: 'post',
    data
  })
}

// 更新景点
export function updateScenicSpot(data: any) {
  return request({
    url: '/api/scenic-spot/update',
    method: 'put',
    data
  })
}

// 删除景点
export function deleteScenicSpot(id: number) {
  return request({
    url: '/api/scenic-spot/delete/' + id,
    method: 'delete'
  })
}
