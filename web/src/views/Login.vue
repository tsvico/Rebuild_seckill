<template>
  <div class="login-box">
    <el-form
      class="login-from"
      :rules="rules"
      :model="loginForm"
      auto-complete="off"
      label-position="left"
    >
      <h3 class="login-title">秒杀系统登录</h3>
      <el-form-item prop="mobile" label="用户名">
        <el-input
          type="text"
          v-model="loginForm.mobile"
          placeholder="用户名"
          prefix-icon="el-icon-user"
        ></el-input>
      </el-form-item>
      <el-form-item prop="password" label="密码">
        <el-input
          type="password"
          v-model="loginForm.password"
          placeholder="请输入密码"
          @keyup.enter.native="handleLogin"
          prefix-icon="el-icon-lock"
          show-password
        />
      </el-form-item>
      <el-form-item prop="verifyCodetoken">
        <el-input
          type="text"
          v-model="loginForm.verifyCodeActual"
          placeholder="验证码"
          clearable
          maxlength="4"
          style="float: left; max-width: 122px;"
        >
          <i slot="prefix" class="el-icon-picture-outline"></i>
        </el-input>
        <span @click="ReplaceVerifyCode">
          <img :src="verifyCodeActual"  fit="contain" style="margin-left: 10px;"></img>
        </span>
      </el-form-item>
      <!-- <el-checkbox class="login_remember" v-model="checked" label-position="left">记住密码</el-checkbox> -->
      <el-form-item>
        <el-button class="login-button" type="primary" @click="handleLogin">登录</el-button>
      </el-form-item>
    </el-form>
    <!-- <div class="register-box">
      <el-button type="text" @click="handleDialogFormVisible">注册用户</el-button>
    </div> -->
    <el-dialog title="用户注册" :visible.sync="dialogFormVisible" width="30%">
      <el-form :model="registerForm">
        <el-form-item label="手机号" :label-width="formLabelWidth">
          <el-input v-model="registerForm.mobile"></el-input>
        </el-form-item>
        <el-form-item label="昵称" :label-width="formLabelWidth">
          <el-input v-model="registerForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth">
          <el-input v-model="registerForm.password"></el-input>
        </el-form-item>
        <el-form-item label="验证码" :label-width="formLabelWidth">
          <el-input v-model="registerForm.verifyCodeActual"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleRegister">注册</el-button>
        <el-button @click="dialogFormVisible = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import saltMD5 from '@/utils/md5Util'

import { register } from '@/utils/api/miaosha'
export default {
  name: 'Login',
  created: function () {
    this.$emit('header', false)
    this.$emit('footer', false)
  },
  data () {
    return {
      rules: {
        mobile: [
          {
            required: true,
            message: '请输入用户名！',
            trigger: 'blur'
          }
        ],
        password: [
          {
            required: true,
            message: '请输入密码！',
            trigger: 'blur'
          }
        ]
      },
      loginForm: {
        mobile: '',
        password: '',
        verifyCodeActual: ''
      },
      registerForm: {
        mobile: '',
        nickname: '',
        password: '',
        verifyCodeActual: ''
      },
      verifyCodeActual: 'http://localhost:8666/service-other/kaptcha',
      dialogFormVisible: false,
      formLabelWidth: '60px'
    }
  },
  methods: {
    // 登录
    handleLogin () {
      const md5LoginForm = {
        username: this.loginForm.mobile,
        password: saltMD5(this.loginForm.password),
        verifyCodeActual: this.loginForm.verifyCodeActual
      }
      console.log(md5LoginForm)
      this.$store
        .dispatch('user/loginByUsername', md5LoginForm)
        .then(() => {
          console.log('登陆成功')
          // 获取用户信息
          this.userInfo()
        })
        .catch(error => {
          this.ReplaceVerifyCode()
          this.$message.error(error) // 登录失败提示错误
        })
    },
    // 注册
    handleRegister () {
      this.dialogFormVisible = false
      const md5RegisterForm = {
        username: this.registerForm.mobile,
        nickname: this.registerForm.nickname,
        password: saltMD5(this.registerForm.password)
      }
      register(md5RegisterForm)
        .then(() => {
          this.$router.push({
            path: '/login'
          })
        })
        .catch(error => {
          this.$message.error('注册失败')
          console.log(error)
        })
    },
    handleDialogFormVisible () {
      this.registerForm = {
        username: '',
        nickname: '',
        password: ''
      }
      this.dialogFormVisible = true
    },
    // 更换验证码
    ReplaceVerifyCode () {
      this.verifyCodeActual =
        'http://localhost:8666/service-other/kaptcha?getTime=' + Math.random()
    },
    userInfo () {
      this.$store
        .dispatch('user/getUserInfo')
        .then(() => {
          this.$router.push({
            path: '/'
          }) // 获取成功后跳转到首页
        })
        .catch(error => {
          this.ReplaceVerifyCode()
          this.$message.error(error) // 登录失败提示错误
        })
    }
  },
  mounted () {
    this.$store
      .dispatch('user/getUserInfo')
      .then(() => {
        this.$router.push({
          path: '/'
        }) // 获取成功后跳转到首页
      })
      .catch(error => {
        console.log(error)
        // nthis.$message.error(error)
      })
  }
}
</script>

<style scoped>
.login-box {
  border-radius: 15px;
  margin: 130px auto;
  width: 40%;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}
@media screen and (max-width: 1210px) {
  .login-box {
    width: 50%;
  }
}
.login-from {
  margin: auto;
  width: 70%;
}

.login-title {
  margin: 0 auto 30px auto;
  text-align: center;
  color: #505458;
}

.login_remember {
  margin: 0 0 20px 0;
  text-align: left;
}

.login-button {
  width: 100%;
}

.register-box {
  margin: auto;
  width: 70%;
}
</style>
