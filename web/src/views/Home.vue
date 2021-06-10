<template>
  <div class="container">
    <!-- 产品列表 -->

    <el-breadcrumb separator="/">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/' }">
        <a  class="el-icon-date" href="javascript:void(0);">商品列表</a>
      </el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>
    <el-row>
      <el-col
        :span="5"
        v-for="(o, index) in tableData"
        :key="index"
        style="margin-top: 30px;"
      >
        <el-card :body-style="{ padding: '0px' }" >
          <img :src="o.goodsImg | baseImg" class="image" style="height: 221px" @click="handleClick(o.id)" />
          <div style="padding: 14px;">
            <span>{{o.goodsName}}</span>
            <div class="bottom clearfix">
              <del>
                <span class="price">{{o.goodsPrice}}</span>
              </del>
              <span style="font-size: x-large; color:#ee0000">
                ￥{{o.miaoshaPrice}}
              </span>
              <span class="nub">仅剩{{o.stockCount}}</span>
              <el-button type="text" @click="handleClick(o.id)" class="button">详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!--尾部列表-->
    <el-pagination background layout="prev, pager, next" :total="1"></el-pagination>
  </div>
</template>

<script>
import { getAllMiaoshaGoods } from '@/utils/api/miaosha'
import { removeUser } from '@/utils/auth'
export default {
  name: 'home',
  data () {
    return {
      tableData: []
    }
  },
  methods: {
    handleClick (id) {
      this.$router.push({
        path: `/goods/${id}`
      })
    }
  },
  mounted () {
    getAllMiaoshaGoods().then(response => {
      console.log(response)
      if (response && response.data.code === 0) {
        let data = response.data.data
        this.tableData = data.goodsList
        console.log(data)
        console.log(this.tableData.data)
      } else {
        removeUser()
        this.$message.error(response.data.msg)
      }
    })
      .catch(err => {
        this.$message.error(err)
        console.log(err) // 这里catch
      })
  },
  created: function () {
    this.$emit('header', true)
    this.$emit('footer', false)
  }
}
</script>

<style scoped>
.el-col{
  float: none;
  display: inline-block;/*一行排多个*/
  vertical-align: top; /*因高度不一致，靠顶部对齐*/
  margin: 10px;
}
.el-card {
    border: 0px solid #EBEEF5;
}
.price {
  color: #808080;
  font-size: small;
}

.el-pagination {
  text-align: center;
}
.nub {
  /*position: absolute;*/
  right: 13px;
  color: #999999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.button {
  /*padding: 0;*/
  float: right;
}

.image {
  width: 100%;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}
</style>
