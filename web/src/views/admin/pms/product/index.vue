<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          @click="handleSearchList()"
          type="primary"
          size="small">
          查询结果
        </el-button>
        <el-button
          style="float: right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="商品名称"></el-input>
          </el-form-item>
          <el-form-item label="商品货号：">
            <el-input style="width: 203px" v-model="listQuery.productSn" placeholder="商品货号"></el-input>
          </el-form-item>
          <el-form-item label="上架状态：">
            <el-select v-model="listQuery.publishStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in publishStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        class="btn-add"
        @click="handleAddProduct()"
        size="mini">
        添加
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="productTable"
                :data="list"
                style="width: 100%"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="商品图片" width="120" align="center">
          <template slot-scope="scope"><img style="height: 80px" :src="scope.row.goodsImg | baseImg"></template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">
            <p>{{scope.row.goodsName}}</p>
            <p>品牌：{{scope.row.goodsTitle}}</p>
          </template>
        </el-table-column>
        <el-table-column label="价格/货号" width="120" align="center">
          <template slot-scope="scope">
            <p>价格：￥{{scope.row.goodsPrice}}</p>
            <p>货号：NULL</p>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="140" align="center">
          <template slot-scope="scope">
            <p>上架：
              <el-switch
                @change="handlePublishStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.publishStatus">
              </el-switch>
            </p>
          </template>
        </el-table-column>
        <el-table-column label="库存数量" width="100" align="center">
          <template slot-scope="scope">{{scope.row.goodsStock}}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <p>
              <el-button
                size="mini"
                @click="handleShowProduct(scope.$index, scope.row)">查看
              </el-button>
              <el-button
                size="mini"
                @click="handleUpdateProduct(scope.$index, scope.row)">编辑
              </el-button>
            </p>
            <p>
              <el-button
                size="mini"
                @click="handleShowLog(scope.$index, scope.row)">日志
              </el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除
              </el-button>
            </p>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-select
        size="small"
        v-model="operateType" placeholder="批量操作">
        <el-option
          v-for="item in operates"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button
        style="margin-left: 20px"
        class="search-button"
        @click="handleBatchOperate()"
        type="primary"
        size="small">
        确定
      </el-button>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>
<script>
import {
  fetchList,
  updateDeleteStatus,
  updatePublishStatus
} from '@/utils/api/product'

const defaultListQuery = {
  keyword: null,
  pageNum: 1,
  pageSize: 5,
  publishStatus: null,
  productSn: null,
  productCategoryId: null,
  brandId: null
}
export default {
  name: 'productList',
  data () {
    return {
      operates: [
        {
          label: '商品上架',
          value: 'publishOn'
        },
        {
          label: '商品下架',
          value: 'publishOff'
        },
        {
          label: '移入回收站',
          value: 'recycle'
        }
      ],
      operateType: null,
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: true,
      selectProductCateValue: null,
      multipleSelection: [],
      brandOptions: [],
      publishStatusOptions: [{
        value: 1,
        label: '上架'
      }, {
        value: 0,
        label: '下架'
      }]
    }
  },
  created () {
    this.getList()
  },
  watch: {
    selectProductCateValue: function (newValue) {
      // eslint-disable-next-line eqeqeq
      if (newValue != null && newValue.length == 2) {
        this.listQuery.productCategoryId = newValue[1]
      } else {
        this.listQuery.productCategoryId = null
      }
    }
  },
  methods: {
    getProductSkuSp (row, index) {
      let spData = JSON.parse(row.spData)
      if (spData != null && index < spData.length) {
        return spData[index].value
      } else {
        return null
      }
    },
    // 请求商品列表
    getList () {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.listLoading = false
        if (response.data.code === 0) {
          let data = response.data.data
          this.list = data.goodsList
          this.total = data.goodsList.length
        }
      })
    },
    handleSearchList () {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleAddProduct () {
      this.$router.push({path: '/admin/pms/addProduct'})
    },
    handleBatchOperate () {
      if (this.operateType == null) {
        this.$message({
          message: '请选择操作类型',
          type: 'warning',
          duration: 1000
        })
        return
      }
      if (this.multipleSelection == null || this.multipleSelection.length < 1) {
        this.$message({
          message: '请选择要操作的商品',
          type: 'warning',
          duration: 1000
        })
        return
      }
      this.$confirm('是否要进行该批量操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = []
        for (let i = 0; i < this.multipleSelection.length; i++) {
          ids.push(this.multipleSelection[i].id)
        }
        switch (this.operateType) {
          case this.operates[0].value:
            this.updatePublishStatus(1, ids)
            break
          case this.operates[1].value:
            this.updatePublishStatus(0, ids)
            break
          case this.operates[6].value:
            break
          case this.operates[7].value:
            this.updateDeleteStatus(1, ids)
            break
          default:
            break
        }
        this.getList()
      })
    },
    handleSizeChange (val) {
      this.listQuery.pageNum = 1
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange (val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    handlePublishStatusChange (index, row) {
      let ids = []
      ids.push(row.id)
      this.updatePublishStatus(row.publishStatus, ids)
    },
    handleResetSearch () {
      this.selectProductCateValue = []
      this.listQuery = Object.assign({}, defaultListQuery)
    },
    handleDelete (index, row) {
      this.$confirm('是否要进行删除操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = []
        ids.push(row.id)
        this.updateDeleteStatus(1, ids)
      })
    },
    handleUpdateProduct (index, row) {
      // this.$router.push({path: '/admin/pms/updateProduct', query: {id: row.id}})
    },
    handleShowProduct (index, row) {
      console.log('handleShowProduct', row)
    },
    handleShowLog (index, row) {
      console.log('handleShowLog', row)
    },
    updatePublishStatus (publishStatus, ids) {
      let params = new URLSearchParams()
      params.append('ids', ids)
      params.append('publishStatus', publishStatus)
      updatePublishStatus(params).then(response => {
        this.$message({
          message: '修改成功',
          type: 'success',
          duration: 1000
        })
      })
    },
    updateDeleteStatus (deleteStatus, ids) {
      let params = new URLSearchParams()
      params.append('ids', ids)
      params.append('deleteStatus', deleteStatus)
      updateDeleteStatus(params).then(response => {
        this.$message({
          message: '删除成功',
          type: 'success',
          duration: 1000
        })
      })
      this.getList()
    }
  }
}
</script>
<style></style>
