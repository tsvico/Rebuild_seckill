const isNullOrEmpty = function (val) {
  if (val == null || val == '' || typeof (val) === undefined) {
    return true
  } else {
    return false
  }
}

const formatDate = (time, fmt) => {
  let date = new Date(time)
  if (isNullOrEmpty(fmt)) {
    fmt = 'yyyy-MM-dd hh:mm:ss'
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  let o = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  }
  for (let k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      let str = o[k] + ''
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str))
    }
  }
  return fmt
}

function padLeftZero (str) {
  return ('00' + str).substr(str.length)
}

const baseImg = function (imgUrl) {
  if (isNullOrEmpty(imgUrl)) {
    return 'https://iph.href.lu/100x25?text=NULL'
  }
  if (imgUrl.indexOf('http') !== -1) {
    return imgUrl
  }
  return 'http://127.0.0.1:8666/service-other' + imgUrl
}
// 超出长度使用省略号
const ellipsis = function (value, len) {
  if (!value) return ''
  // 默认值设置为8
  let strlen = len || 8
  if (value.length > strlen) {
    return value.slice(0, strlen) + '...'
  }
  return value
}
export {
  isNullOrEmpty,
  formatDate,
  baseImg,
  ellipsis
}
