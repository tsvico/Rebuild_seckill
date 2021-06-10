// 工具类

// 过滤器中使用
// import {formatDate} from './js/date.js'; <span>{{date | formatDate}}</span>
// v-model中格式化时间（过滤器就失效了）
// formatTimeToStr(xxx, 'yyyy-MM-dd')
// 文件引入（注意：由于是函数，故名字要和函数的名字一致）
// import { formatDate } from '@/common/commonUtil.js'
// 添加到过滤器中
// filters: {
//     formatDate(time) {
//     var date = new Date(time);
//     return formatDate(date, 'yyyy-MM-dd');
//    }
// },
// 在HTML中使用
// // item.createdate是后台数据~~
// <div class="bottomTimee fz12 c_9a">{{item.createdate | formatDate}}</div>
// 在提交时候使用
// let nowDate = formatDate(new Date(), 'yyyy-MM-dd hh:mm')
// 在绑定属性中使用
// <mt-cell title="开始时间" :value="startDate | formatDate"></mt-cell>
export function formatDate (date, fmt) {
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
};

function padLeftZero (str) {
  return ('00' + str).substr(str.length)
}

export function str2Date (dateStr, separator) {
  if (!separator) {
    separator = '-'
  }
  let dateArr = dateStr.split(separator)
  let year = parseInt(dateArr[0])
  let month
  // 处理月份为04这样的情况
  if (dateArr[1].indexOf('0') == 0) {
    month = parseInt(dateArr[1].substring(1))
  } else {
    month = parseInt(dateArr[1])
  }
  let day = parseInt(dateArr[2])
  let date = new Date(year, month - 1, day)
  return date
}
