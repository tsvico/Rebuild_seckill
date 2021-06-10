import request from '@/utils/request'

export function fetchList (params) {
  return request({
    url: '/service-other/flashSession/list',
    method: 'get',
    params: params
  })
}

export function fetchSelectList (params) {
  return request({
    url: '/service-other/flashSession/selectList',
    method: 'get',
    params: params
  })
}

export function updateStatus (id, params) {
  return request({
    url: '/service-other/flashSession/update/status/' + id,
    method: 'post',
    params: params
  })
}
