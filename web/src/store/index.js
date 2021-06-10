import Vue from 'vue'
import Vuex from 'vuex'

import user from './modules/user'
import admin from './modules/admin'
import http from './modules/http'
import app from './modules/app'
import getters from './getters'
import permission from './modules/permission'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    app,
    user,
    http,
    admin,
    permission
  },
  getters
})
