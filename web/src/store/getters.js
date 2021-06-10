const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.admin.avatar,
  name: state => state.admin.nickname,
  roles: state => state.admin.roles,
  addRouters: state => state.permission.addRouters,
  routers: state => state.permission.routers
}
export default getters
