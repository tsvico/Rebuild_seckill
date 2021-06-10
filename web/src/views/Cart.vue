<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>购物车</el-breadcrumb-item>
    </el-breadcrumb>
    <br>
    <el-table :data="tableData" border style="width: 100%" @selection-change="selected">
      <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column width="150" v-if="false" prop="id"></el-table-column>
      <el-table-column label="商品名称">
        <template slot-scope="scope">
          <div style="margin-left: 30px">
            <img :src="scope.row.goodImg | baseImg" style="height: 50px;width: 50px" />
            <span style="font-size: 14px;">{{scope.row.goodsName | ellipsis(6)}}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="150" prop="goodsPrice"></el-table-column>
      <el-table-column label="数量" width="200">
        <template slot-scope="scope">
          <div>
            <el-input v-model="scope.row.goodsCount" :disabled="true" @change="handleInput(scope.row)">
              <el-button slot="prepend" @click="del(scope.row)">
                <i class="el-icon-minus"></i>
              </el-button>
              <el-button slot="append" @click="add(scope.row)">
                <i class="el-icon-plus"></i>
              </el-button>
            </el-input>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="小计" width="150" prop="goodTotal">
      </el-table-column>
      <el-table-column label="创建时间" width="150">
        <template slot-scope="scope">
          {{scope.row.createDate|formatDate}}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleDetail(scope.$index, scope.row)">
            详情
            <i class="el-icon-pear el-icon--right"></i>
          </el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">
            删除
            <i class="el-icon-delete el-icon--right"></i>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <br />
    <el-button type="info" :disabled="moneyTotal==0" style="float: right">{{"商品总额：" + moneyTotal}}</el-button>
  </div>
</template>

<script>
import {
  getCartOrders
} from '@/utils/api/miaosha'
export default {
  created: function () {
    this.$emit('header', true)
    this.$emit('footer', false)
  },
  data () {
    return {
      tableData: [
        {
          id: '',
          goodImg: 'http://i1.mifile.cn/a1/pms_1474859997.10825620!80x80.jpg',
          goodsName: '小米手环2',
          goodsPrice: 149,
          goodsCount: 1,
          goodTotal: 149,
          createDate: new Date()
        }
      ],
      moneyTotal: 0,
      multipleSelection: []
    }
  },
  mounted () {
    getCartOrders().then(response => {
      if (response.data) {
        if (response.data.code === 0) {
          this.tableData = [...this.tableData, ...response.data.data]
          this.tableData.forEach(item => {
            // 新增属性
            item.goodTotal = item.goodsCount * item.goodsPrice
          })
          console.log(this.tableData)
        } else {
          this.$message.error(response.data.msg)
        }
      }
    }).catch(error => {
      this.$message.error('获取购物车信息失败')
      console.log(error)
    })
  },
  methods: {
    handleDetail (index, row) {
      console.log(index, row)
      if (row.id) {
        this.$router.push({
          path: `/order/${row.id}`
        })
      } else {
        this.$message.warning('测试用例,暂时没有数据')
      }
    },
    handleDelete (index, row) {
      this.$confirm('确定删除该商品？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 删除数组中指定的元素
          this.tableData.splice(index, 1)
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    handleInput: function (value) {
      if (value.number == null || value.number === '') {
        value.number = 1
      }
      value.goodTotal = (value.number * value.price).toFixed(2) // 保留两位小数
      // 增加商品数量也需要重新计算商品总价
      this.selected(this.multipleSelection)
    },
    add: function (addGood) {
      this.$message.info('只允许抢购数量1')
      return
      // 输入框输入值变化时会变为字符串格式返回到js
      // 此处要用v-model，实现双向数据绑定
      // eslint-disable-next-line no-unreachable
      if (typeof addGood.number === 'string') {
        addGood.number = parseInt(addGood.number)
      }
      addGood.number += 1
    },
    del: function (delGood) {
      if (typeof delGood.number === 'string') {
        delGood.number = parseInt(delGood.number)
      }
      if (delGood.number > 1) {
        delGood.number -= 1
      }
    },
    // 返回的参数为选中行对应的对象
    selected: function (selection) {
      this.multipleSelection = selection
      this.moneyTotal = 0
      // 此处不支持forEach循环，只能用原始方法了
      for (var i = 0; i < selection.length; i++) {
        // 判断返回的值是否是字符串
        if (typeof selection[i].goodTotal === 'string') {
          selection[i].goodTotal = parseInt(selection[i].goodTotal)
        }
        this.moneyTotal += selection[i].goodTotal
      }
    }
  }
}
</script>
<style scoped>
.el-table th.gutter{
    display: table-cell!important;
}
.el-table colgroup.gutter{
    display: table-cell!important;
}
</style>
