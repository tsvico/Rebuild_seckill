<template>
  <div id="app" v-cloak>
    <app-header v-if="header_show"></app-header>
    <router-view v-on:header="header" v-on:footer="footer"></router-view>
    <app-footer v-if="footer_show"></app-footer>
    <!-- <img src="./assets/logo.png"> -->
    <!--<router-view />-->
  </div>
</template>

<script>
import Header from './components/header'
import Footer from './components/footer'

export default {
  name: 'App',
  data () {
    return {
      header_show: false,
      footer_show: false
    }
  },
  components: {
    'app-header': Header,
    'app-footer': Footer
  },
  methods: {
    // 是否显示头部
    header: function (bool) {
      console.log(bool)
      this.header_show = bool
    },
    // 是否显示底部
    footer: function (bool) {
      this.footer_show = bool
    },
    created: function () {
      if (document.documentElement.clientWidth < 640) {
        // require('assets/style/mobile.scss');
      }
    }
  }
}
</script>

<style>
[v-cloak] {
	display: none !important;
}
  /* #app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
} */
</style>
