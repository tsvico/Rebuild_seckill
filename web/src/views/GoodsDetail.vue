<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/' }">商品列表</el-breadcrumb-item>
      <el-breadcrumb-item>
        <a class="el-icon-date" href="/">商品详情</a>
      </el-breadcrumb-item>
    </el-breadcrumb>
    <hr />
    <el-card>
      <div class="image-box">
        <el-image :src="goods.goodsImg | baseImg" class="image"></el-image>
      </div>
      <div class="info-box">
        <ul>
          <li v-if="miaoshaStatus != 2">当前时间：{{currentTime}}</li>
          <span v-if="miaoshaStatus === 0">
            <li>开始时间: {{goods.startDate | formatDate}}</li>
            <h4>秒杀倒计时：{{countDownShow}}</h4>
          </span>
          <span v-else-if="miaoshaStatus === 1">
            <li>结束时间: {{goods.endDate | formatDate}}</li>
            <h4>秒杀进行中！</h4>
          </span>
          <span v-else>
            <!--miaoshaStatus === 2-->
            <li>秒杀时间：{{goods.startDate | formatDate}}- {{goods.endDate | formatDate}}</li>
            <h4>您已错过秒杀！现在为原价购买</h4>
          </span>
          <li>
            <p>商品名称: {{goods.goodsName}}</p>
          </li>
          <li>
            <p>
              商品原价:
              <span v-bind:class="miaoshaStatus <= 1?'del':''">￥{{goods.goodsPrice}}</span>
            </p>
          </li>
          <li>
            <p>商品详情:</p>
            {{goods.goodsDetail}}
          </li>
          <li>
            <p>秒杀价: <span v-bind:class="miaoshaStatus > 1?'del':''">￥{{goods.miaoshaPrice}}</span></p>
          </li>
          <li>
            <h3>当前库存数量: {{miaoshaStatus > 1 ? goods.goodsStock : goods.stockCount}}</h3>
          </li>
          <li>
            <div style="height: 40px;margin: 10;" v-if="miaoshaStatus === 1">
              <div style="height: 40px; float: left;">
                <el-input size="small" type="number" v-model.number="verifyCodeInput" placeholder="请计算验证码"></el-input>
              </div>
              <div>
                <el-image
                  style=" margin-left: 30px;"
                  :src="verifyCode | baseImg"
                  @click="ReplaceVerifyCode"
                  title="点击刷新验证码"
                ></el-image>
              </div>
            </div>
          </li>
          <li>
            <Vcode
            :imgs="[Img1,Img2,Img3,Img4,Img5,Img6,Img7,Img8,Img9,Img10]"
            :show="isShow" @success="success" @close="close" />
            <el-button
              type="primary"
              class="button"
              :disabled="seckillDisabled"
              @click="submit"
            >立即秒杀</el-button>
            <el-button v-if="miaoshaStatus > 1"
              type="primary"
              class="button"
              :disabled="!seckillDisabled"
              @click="buy"
            >购买</el-button>
          </li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script>
import { formatDate } from '@/utils/common.js'
import {
  seckill,
  getMiaoshaGoods,
  getMiaoshaPath,
  getMiaoshaResult
} from '@/utils/api/miaosha'
import { getToken } from '@/utils/auth'
import Vcode from 'vue-puzzle-vcode'
import Img1 from '@/assets/images/1.png'
import Img2 from '@/assets/images/2.png'
import Img3 from '@/assets/images/3.png'
import Img4 from '@/assets/images/4.png'
import Img5 from '@/assets/images/5.png'
import Img6 from '@/assets/images/6.png'
import Img7 from '@/assets/images/7.png'
import Img8 from '@/assets/images/8.png'
import Img9 from '@/assets/images/9.png'
import Img10 from '@/assets/images/10.png'

export default {
  name: 'detail',
  data () {
    return {
      goods: {},
      goodsId: -1,
      remainSeconds: -1,
      currentTime: formatDate(new Date(), 'yyyy-MM-dd hh:mm'),
      timer: {},
      verifyCode: 'https://iph.href.lu/100x25?text=%E9%AA%8C%E8%AF%81%E7%A0%81',
      baseverifyCode: '',
      verifyCodeInput: '',
      miaoshaStatus: 2,
      seckillDisabled: true,
      isShow: false, // 显示滑块验证码
      Img1,
      Img2,
      Img3,
      Img4,
      Img5,
      Img6,
      Img7,
      Img8,
      Img9,
      Img10
    }
  },
  components: {
    Vcode
  },
  computed: {
    countDownShow () {
      const totalSeconds = this.remainSeconds
      const hours = Math.floor(totalSeconds / 3600)
      const minutes = Math.floor((totalSeconds % 3600) / 60)
      const seconds = totalSeconds % 60
      return hours + ' 时 ' + minutes + ' 分 ' + seconds + ' 秒'
    }
  },
  watch: {
    // 监听，当remainSeconds变化时miaoshaStatus也变化
    remainSeconds (val) {
      // console.log(val)
      if (val === 0) {
        this.miaoshaStatus = 1
        this.seckillDisabled = false
        clearInterval(this.timer)
        this.timer = setInterval(() => {
          this.currentTime = formatDate(new Date(), 'yyyy-MM-dd hh:mm:ss')
        }, 1000)
      }
    },
    currentTime (val) {
      // console.log(this.miaoshaStatus)
      if (this.miaoshaStatus === 1) {
        // 处于秒杀内
        // console.log('val', new Date(val))
        // console.log('结束', new Date(this.goods.endDate))
        // console.log(new Date(val) - new Date(this.goods.endDate))
        if (new Date(val) - new Date(this.goods.endDate) > 0) {
          this.miaoshaStatus = 2
          this.seckillDisabled = true
          clearInterval(this.timer)
        }
      }
    }
  },
  mounted () {
    this.goodsId = this.$router.currentRoute.params.id
    this.verifyCode =
      '/seckill/verifyCode?goodsId=' +
      this.goodsId +
      '&Authorization=' +
      getToken()
    this.baseverifyCode = this.verifyCode
    getMiaoshaGoods(this.goodsId)
      .then(response => {
        var _this = this
        let data = response.data.data
        if (response.data.code !== 0) {
          this.$message.error(response.data.msg)
          return
        }
        _this.goods = data.goodsVo
        console.log(_this.goods)
        _this.remainSeconds = data.remainSeconds
        _this.miaoshaStatus = data.miaoshaStatus
        _this.currentTime = formatDate(new Date(), 'yyyy-MM-dd hh:mm:ss')
        _this.timer = setInterval(() => {
          _this.remainSeconds--
          _this.currentTime = formatDate(new Date(), 'yyyy-MM-dd hh:mm:ss')
        }, 1000)
        if (_this.stockCount <= 0) {
          _this.miaoshaStatus = 2
        }
      })
      .catch(error => {
        this.$message.error('获取商品信息失败！')
        // this.$message.error(error)
        console.log(error)
      })

    /*
      getVerifyCode(this.goodsId).then(response => {
        this.verifyCodeUrl = 'data:image/jpeg;base64,' + response.data.data
        console.log(this.verifyCodeUrl)
      }).catch(error => {
        this.$message.error('获取验证码失败！')
        console.log(error)
      }) */
  },
  methods: {
    submit () {
      if (this.verifyCodeInput.length < 1) {
        this.$message.warning('请输入验证码')
        return
      }
      this.isShow = true
    },
    // 用户通过了验证
    success (msg) {
      this.isShow = false // 通过验证后，需要手动隐藏模态框
      this.handleSeckill()
    },
    // 用户点击遮罩层，应该关闭模态框
    close () {
      this.isShow = false
    },
    buy () {
      this.$message.success('原价购买')
    },
    handleSeckill () {
      if (this.verifyCodeInput.length < 1) {
        this.$message.warning('请输入验证码')
        return
      }
      getMiaoshaPath(this.goodsId, this.verifyCodeInput).then(res => {
        if (res.data.code === 0) {
          // 禁用按钮
          this.seckillDisabled = true
          var data = res.data.data
          const path = data.hash
          seckill(path, this.goodsId)
            .then(resp => {
              if (resp.data.code === 0) {
                this.$message.success('等待秒杀结果...')
                const loading = this.$loading({
                  lock: true,
                  text: '正在提交订单'
                  // spinner: 'el-icon-loading',
                  // background: 'rgba(0, 0, 0, 0.7)'
                })
                const resultTimer = setInterval(() => {
                  getMiaoshaResult(this.goodsId)
                    .then(response => {
                      if (response.data.code === 0) {
                        loading.close()
                        this.$message.success('秒杀成功！')
                        clearInterval(resultTimer)
                        const id = response.data.data
                        if (id === 0) {
                          this.$message.error('秒杀成功，可在购物车查看！')
                          return
                        }
                        this.$router.push({
                          path: `/order/${id}`
                        })
                      } else {
                        this.$message.error(response.data.msg)
                        this.$router.push({
                          path: '/'
                        })
                      }
                    })
                    .catch(error => {
                      loading.close()
                      this.seckillDisabled = false
                      this.$message.error('获取结果失败！')
                      console.log(error)
                    })
                }, 200)
              } else {
                this.ReplaceVerifyCode()
                this.$message.error(resp.data.msg)
              }
            })
            .catch(err => {
              this.$message.error('服务器出错！')
              this.$router.push('/')
              console.log('error: ')
              console.log(err)
            })
        } else {
          this.verifyCodeInput = ''
          this.$message.error(res.data.msg)
        }
      })
    },
    // 更换验证码
    ReplaceVerifyCode () {
      this.verifyCode = this.baseverifyCode + '&getTime=' + Math.random()
    }
  },
  filters: {
    formatDate (time) {
      var date = new Date(time)
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  created: function () {
    this.$emit('header', true)
    this.$emit('footer', false)
  }
}
</script>

<style  scoped>
.el-card {
  margin: auto;
  /*width: 60%;*/
  position: relative;
  height: 560px;
}

.image-box {
  text-align: center;
  width: 360px;
  background: #eee;
  position: absolute;
}

.image {
  width: 350px;
  /*height: 350px;*/
}

.info-box {
  text-align: left;
  width: 500px;
  position: absolute;
  /*top: 0px;*/
  left: 420px;
  /*padding-left: 220px;*/
}

ul li {
  list-style: none;
}

h4 {
  color: red;
}
</style>
