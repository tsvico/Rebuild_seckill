import Cookies from 'js-cookie'

const TokenKey = 'Authorization'

export function getToken () {
  return Cookies.get(TokenKey)
}

export function setToken (token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken () {
  return Cookies.remove(TokenKey)
}

export function getverifyCodetoken () {
  return Cookies.get('verifyCodetoken')
}

export function setUser (user) {
  return sessionStorage.setItem('user', JSON.stringify(user))
}

export function getUser () {
  var userJsonStr = sessionStorage.getItem('user')
  if (userJsonStr != null && userJsonStr != 'undefined') {
    return JSON.parse(userJsonStr)
  }
  return null
}

export function removeUser () {
  sessionStorage.removeItem('user')
}
