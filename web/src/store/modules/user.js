import {
  login,
  getUserInfo
} from '@/utils/api/miaosha'
import {
  login as adminlogin
} from '@/utils/api/admin'
import {
  getToken,
  setToken,
  removeToken,
  setUser
} from '@/utils/auth'

const state = {
  token: getToken(),
  name: ''
}

const mutations = {
  SET_TOKEN (state, token) {
    state.token = token
  },
  SET_NAME (state, name) {
    state.name = name
  }
}

const actions = {
  // 登录
  loginByUsername ({ commit }, user) {
    return new Promise((resolve, reject) => {
      // console.log(user)
      login(user)
        .then(response => {
          // const data = response.data.data  //请求结果
          let res = response.data
          console.log(res)
          if (res && res.code === 0) {
            setToken(res.data.token) // 登陆成功
            commit('SET_TOKEN', res.data.token)
            resolve('成功')
          } else {
            reject(res.msg)
          }
        })
        .catch(error => {
          reject(error)
        })
    })
  },
  // 登录
  loginByAdmin ({ commit }, user) {
    return new Promise((resolve, reject) => {
      console.log(user)
      adminlogin(user)
        .then(response => {
          // const data = response.data.data  //请求结果
          let res = response.data
          console.log(res)
          if (res && res.code === 0) {
            setToken(res.data.token) // 登陆成功
            commit('SET_TOKEN', res.data.token)
            resolve('成功')
          } else {
            reject(res.msg)
          }
        })
        .catch(error => {
          reject(error)
        })
    })
  },
  getUserInfo ({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then(response => {
          let res = response.data
          if (res && res.code !== 0) {
            sessionStorage.clear()
            reject(new Error('验证失败，请重新登陆'))
          }
          setUser(res.data)
          commit('SET_NAME', res.data.username)
          resolve(res.data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },
  logout ({ commit }) {
    return new Promise((resolve, reject) => {
      try {
        removeToken()
        commit('SET_TOKEN', '')
        resolve()
      } catch (error) {
        reject(error)
      }
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
