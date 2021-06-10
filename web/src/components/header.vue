<template>
  <div style="margin-bottom: 10px;">
    <!-- 页面顶部黑条部分-->
    <div id="page_top_black_bar">
      <div class="container" id="black_bar_box">
        <ul>
          <li>
            您好<strong class="user_name" v-if="user">&nbsp;&nbsp;{{user.nickname}}&nbsp;</strong> ,欢迎来到秒杀商城!
          </li>
          <li v-if="!user">
            <router-link to="/login_register">登录</router-link>
          </li>
          <li v-if="!user">
            <router-link to="/login_register">注册</router-link>
            <!-- <a href="#">注册</a> -->
          </li>
        </ul>
        <ul>
          <li>
            <router-link to="/Cart">我的订单</router-link>
          </li>
          <li>
            <a href="#">帮助中心</a>
          </li>
          <li v-if="user.role==1">
            <!-- <span class="fa fa-id-badge"></span> -->
            <span class="el-icon-setting" style="color:red;"></span>
            &nbsp;
            <span>
              <router-link to="/admin" style="color:red;">后台管理</router-link>
            </span>
          </li>
        </ul>
      </div>
    </div>

    <!-- 页面顶部中间 logo 搜索框 购物车 部分 -->
    <div id="page_top_middle_bar" class="container">
      <div id="page_top_middle_bar_logo">
        <a href="/" title="秒杀商城" >
          <img
            src="/static/images/logo.png"
            alt="秒杀logo"
            title="秒杀商城 | 超低价购物 | 极速体验"
          />
        </a>
      </div>
      <div id="page_top_middle_bar_search">
        <div id="page_top_middle_bar_search_top">
          <el-input placeholder="请输入内容" v-model="search_content" class="input-with-select">
            <el-select v-model="select_content" slot="prepend" placeholder="请选择">
              <el-option label="普通商品" value="1"></el-option>
              <el-option label="秒杀商品" value="2"></el-option>
            </el-select>
            <el-button slot="append" icon="el-icon-search"></el-button>
          </el-input>
        </div>
        <div id="page_top_middle_bar_search_bottom">
          <ul>
            <li>热门搜索:&#x3000;</li>
            <el-tag
              v-for="tag in tags"
              :key="tag.name"
              closable
              :type="tag.type"
              @close="handleClose(tag)">
              {{tag.name}}
            </el-tag>
          </ul>
        </div>
      </div>
      <div id="page_top_middle_bar_shopping_cart">
        <router-link to="/cart">
          <el-badge is-dot class="item">
            <i class="el-icon-shopping-cart-2"></i>
          </el-badge>
        </router-link>
      </div>
    </div>

    <div class="content content-nav-base commodity-content container">
      <div class="main-nav">
        <div class="inner-cont0">
          <div class="inner-cont1 w1200">
            <div class="inner-cont2">
              <a href="#/" class="active">所有商品</a>
              <a href="#/404">今日团购</a>
              <a href="#/404">商品资讯</a>
              <a href="#/404">关于我们</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getUser, removeToken } from '@/utils/auth'
export default {
  name: 'app-header',
  data () {
    return {
      user: getUser(),
      // 搜索内容
      search_content: '',
      // 选择内容
      select_content: '',
      tags: [
        { name: '1元抢小米10Pro', type: '' },
        { name: '0.3抢华为Mate 30', type: 'success' },
        { name: '0.5抢一加8', type: 'info' }
        /* { name: '5元抢小鸭电冰箱', type: 'warning' } */
      ]
    }
  },
  mounted () {
    console.log(this.user)
    // var _this = this
    if (this.user == null) {
      this.$store
        .dispatch('user/getUserInfo')
        .then(() => {
          this.user = getUser()
        })
        .catch(error => {
          console.log('头部判断登录失败')
          // 删除凭证
          removeToken()
          this.$message.error(error) // 登录失败提示错误
          this.$router.push({
            path: 'login'
          })
        })
    }
  },
  methods: {
    handleClose (tag) {
      this.tags.splice(this.tags.indexOf(tag), 1)
    }
  },
  created () {}
}
</script>

<style scoped>
  .el-tag + .el-tag {
    margin-left: 10px;
  }
#page_top_black_bar {
  width: 100%;
  height: 40px;
  background-color: #f5f5f5;
}
#black_bar_box {
  display: flex;
  justify-content: space-between;
  height: 100%;
}
#page_top_middle_bar{
  padding: 20px 0;
}
#black_bar_box ul,
#page_top_middle_bar_search_bottom ul{
  list-style: none;
  padding: 0;
  margin: 0;
  height: 100%;
  display: flex;
  align-items: center;
}
#black_bar_box ul li,
#page_top_middle_bar_search_bottom ul li{
  padding-right: 10px;
}
#black_bar_box ul li::after,
#page_top_middle_bar_search_bottom ul li::after {
  /*content: "|";*/
  padding-left: 10px;
}
#black_bar_box ul li,
#black_bar_box ul li  a {
  font-size: 0.8rem;
  color: black;
  text-decoration: none;
  font-weight: 300;
  display: flex;
  align-items: center;
}
#black_bar_box ul li  a:hover {
  text-decoration: underline;
}
#page_top_middle_bar {
  /*height: 80px;*/
  background-color: white;
  display: flex;
  justify-content: space-between;
}
#page_top_middle_bar_logo {
  width: 350px;
}
#page_top_middle_bar_logo img {
  width: 100%;
}
#page_top_middle_bar_search {
  width: 500px;
  /* background-color: red; */
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
#page_top_middle_bar_search_top {
  height: 70%;
  /* background-color: orange; */
  display: flex;
  align-items: center;
}
#page_top_middle_bar_search_bottom ul li,
#page_top_middle_bar_search_bottom ul li  a{
   font-size: 0.8rem;
  color: grey;
  text-decoration: none;
  font-weight: 400;
  display: flex;
  align-items: center;
}
#page_top_middle_bar_shopping_cart{
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  color: rgb(176, 158, 122);
}

/*end-header*/
.content .inner-cont1 {
  height: 36px;
  line-height: 36px;
}
.content .inner-cont2 {
  padding-left: 208px;
}
.content .inner-cont2 a {
  display: inline-block;
  font-size: 14px;
  color: #010101;
  padding: 0 32px;
  line-height: 36px;
  float: left;
}
.content .inner-cont2 a.active,
.content .inner-cont2 a:hover {
  color: #cfb2f6;
}
.content-nav-base .main-nav {
  border-bottom: 1px solid #cfb1f7;
}
.content-nav-base .main-nav .inner-cont2 {
  padding-left: 40px;
}
.content-nav-base .main-nav .inner-cont2 a {
  padding: 0;
  margin-right: 75px;
}
</style>
