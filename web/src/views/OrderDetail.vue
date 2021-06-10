<template>
<div class="container">
      <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/cart'}">购物车</el-breadcrumb-item>
      <el-breadcrumb-item>订单详情</el-breadcrumb-item>
    </el-breadcrumb>
    <hr>
    <br>
<el-table
   :data="tableData"
    border
   >
    <el-table-column
      prop="id"
      label="订单号">
    </el-table-column>
     <el-table-column
      prop="userId"
      label="用户">
    </el-table-column>
    <el-table-column
      prop="goodsName"
      label="商品名称">
    </el-table-column>
    <el-table-column
      prop="goodsPrice"
      label="单价">
    </el-table-column>
    <el-table-column
      prop="goodsCount"
      label="数量">
    </el-table-column>
    <el-table-column
      prop="totalCost"
      label="总额">
    </el-table-column>
    <el-table-column
      prop="createDate"
      label="创建时间"
      width="200">
    </el-table-column>
    <el-table-column
      prop="status"
      label="支付状态" :formatter="stateFormat">
    </el-table-column>
    <el-table-column>
      <template slot-scope="scope">
        <el-button @click="handleClick(scope.row.goodsName)" type="text" size="small">支付</el-button>
      </template>
    </el-table-column>
  </el-table>
</div>
</template>

<script>
import {getOrder} from '@/utils/api/miaosha'
import {formatDate} from '@/utils/common'
//import mailTable from '../components/tableComponent'

export default {
  name: 'orderDetail',
  data () {
    return {
      tableData: []
    }
  },
  components: {
    //mailTable,
  },
  created: function () {
    this.$emit('header', true)
    this.$emit('footer', false)
  },
  methods: {
    stateFormat (row, column) {
      let value = row.state
      if (value === 1) {
        return '待发货'
      } else if (value === 2) {
        return '已发货'
      } else if (value === 3) {
        return '已收货'
      } else if (value === 4) {
        return '已关闭'
      } else if (value === 5) {
        return '已完成'
      } else {
        return '待付款'
      }
    },
    handleClick (goods) {
      console.log(goods)
      this.$message.success(goods + '支付成功')
    }
  },
  mounted () {
    const orderId = this.$router.currentRoute.params.id
    // console.log(orderId)
    getOrder(orderId).then(response => {
      if (response.data.code === 0) {
        let order = response.data.data.orderInfo
        order.totalCost = order.goodsPrice
        order.createDate = formatDate(new Date(order.createDate), 'yyyy-MM-dd hh:mm:ss')
        //console.log(this.tableData)
        // for(let prop in order) {
        //   this.tableData.push({
        //       key: prop,
        //       value: order[prop]
        //   })
        // }
        this.tableData.push(order)
        console.log(this.tableData)
      } else {
        this.$message.error(response.data.msg)
      }
    }).catch(error => {
      console.log(error)
    })
  }

}
</script>

<style scoped>

</style>
