<template> 
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button size="mini" class="btn-add" @click="handleSelectProduct()" style="margin-left: 20px">添加</el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="productRelationTable" :data="list" style="width: 100%;" v-loading="listLoading" border>
        <el-table-column label="编号" width="140" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">{{scope.row.goodsName}}</template>
        </el-table-column>
        <el-table-column label="商品价格" width="100" align="center">
          <template slot-scope="scope">￥{{scope.row.goodsPrice}}</template>
        </el-table-column>
        <el-table-column label="剩余数量" width="100" align="center">
          <template slot-scope="scope">{{scope.row.stockCount}}</template>
        </el-table-column>
        <el-table-column label="秒杀价格" width="100" align="center">
          <template slot-scope="scope">
            <p v-if="scope.row.miaoshaPrice!==null">
              ￥{{scope.row.miaoshaPrice}}
            </p>
          </template>
        </el-table-column>
        <el-table-column label="秒杀数量" width="100" align="center">
          <template slot-scope="scope">100</template>
        </el-table-column>
        <el-table-column label="限购数量" width="100" align="center">
          <template slot-scope="scope">1</template>
        </el-table-column>
        <el-table-column label="开始时间" width="100" align="center">
          <template slot-scope="scope">{{scope.row.startDate| formatDate}}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleUpdate(scope.$index, scope.row)">编辑
            </el-button>
            <el-button size="mini" type="text" @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum" :page-size="listQuery.pageSize" :page-sizes="[5,10,15]" :total="total">
      </el-pagination>
    </div>
    <el-dialog title="选择商品" :visible.sync="selectDialogVisible" width="50%">
      <el-input v-model="dialogData.listQuery.keyword" style="width: 250px;margin-bottom: 20px" size="small"
        placeholder="商品名称搜索">
        <el-button slot="append" icon="el-icon-search" @click="handleSelectSearch()"></el-button>
      </el-input>
      <el-table :data="dialogData.list" @selection-change="handleDialogSelectionChange" border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="商品名称" align="center">
          <template slot-scope="scope">{{scope.row.goodsName}}</template>
        </el-table-column>
        <el-table-column label="货号" width="160" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="价格" width="120" align="center">
          <template slot-scope="scope">￥{{scope.row.goodsPrice}}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background @size-change="handleDialogSizeChange" @current-change="handleDialogCurrentChange"
          layout="prev, pager, next" :current-page.sync="dialogData.listQuery.pageNum" :page-size="dialogData.listQuery.pageSize"
          :page-sizes="[5,10,15]" :total="dialogData.total">
        </el-pagination>
      </div>
      <div style="clear: both;"></div>
      <div slot="footer">
        <el-button size="small" @click="selectDialogVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="handleSelectDialogConfirm()">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="编辑秒杀商品信息" :visible.sync="editDialogVisible" width="40%">
      <el-form :model="flashProductRelation" ref="flashProductRelationForm" label-width="150px" size="small">
        <el-form-item label="商品名称：">
          <span>{{flashProductRelation.goodsName}}</span>
        </el-form-item>
        <el-form-item label="货号：">
          <span>{{flashProductRelation.id}}</span>
        </el-form-item>
        <el-form-item label="商品价格：">
          <span>￥{{flashProductRelation.goodsPrice}}</span>
        </el-form-item>
        <el-form-item label="秒杀价格：">
          <el-input v-model="flashProductRelation.miaoshaPrice" class="input-width">
            <template slot="prepend">￥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="剩余数量：">
          <span>{{flashProductRelation.goodsStock}}</span>
        </el-form-item>
        <el-form-item label="秒杀数量：">
          <el-input v-model="flashProductRelation.stockCount" class="input-width"></el-input>
        </el-form-item>
        <el-form-item label="时间日期">
          <el-date-picker v-model="miaoshaTime" type="datetimerange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="限购数量：">
          1
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleEditDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {
    fetchList,
    createFlashProductRelation,
    deleteFlashProductRelation,
    updateFlashProductRelation
  } from '@/utils/api/flashProductRelation';
  import {
    fetchList as fetchProductList
  } from '@/utils/api/product';

  import {
    formatDate
  } from '@/utils/common';
  const defaultListQuery = {
    pageNum: 1,
    pageSize: 5,
    flashPromotionId: null,
    flashPromotionSessionId: null
  };
  export default {
    name: 'flashPromotionProductRelationList',
    data() {
      return {
        listQuery: Object.assign({}, defaultListQuery),
        list: null,
        total: null,
        listLoading: false,
        dialogVisible: false,
        selectDialogVisible: false,
        dialogData: {
          list: null,
          total: null,
          multipleSelection: [],
          listQuery: {
            keyword: null,
            pageNum: 1,
            pageSize: 5
          }
        },
        editDialogVisible: false,
        flashProductRelation: {},
        miaoshaTime: [new Date(2000, 10, 10, 10, 10), new Date(2000, 10, 11, 10, 10)]
      }
    },
    created() {
      this.listQuery.flashPromotionId = this.$route.query.flashPromotionId;
      this.listQuery.flashPromotionSessionId = this.$route.query.flashPromotionSessionId;
      this.getList();
    },
    methods: {
      handleSizeChange(val) {
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val;
        this.getList();
      },
      handleSelectProduct() {
        this.selectDialogVisible = true;
        this.getDialogList();
      },
      handleUpdate(index, row) {
        this.editDialogVisible = true;
        this.flashProductRelation = Object.assign({}, row);
        this.miaoshaTime = [new Date(this.flashProductRelation.startDate), new Date(this.flashProductRelation.endDate)]
        console.log("编辑按钮点击", this.flashProductRelation)
      },
      handleDelete(index, row) {
        this.$confirm('是否要删除该商品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteFlashProductRelation(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          });
        });
      },
      handleSelectSearch() {
        this.getDialogList();
      },
      handleDialogSizeChange(val) {
        this.dialogData.listQuery.pageNum = 1;
        this.dialogData.listQuery.pageSize = val;
        this.getDialogList();
      },
      handleDialogCurrentChange(val) {
        this.dialogData.listQuery.pageNum = val;
        this.getDialogList();
      },
      handleDialogSelectionChange(val) {
        this.dialogData.multipleSelection = val;
      },
      handleSelectDialogConfirm() {
        if (this.dialogData.multipleSelection < 1) {
          this.$message({
            message: '请选择一条记录',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        let selectProducts = [];
        for (let i = 0; i < this.dialogData.multipleSelection.length; i++) {
          selectProducts.push({
            productId: this.dialogData.multipleSelection[i].id,
            flashPromotionId: this.listQuery.flashPromotionId,
            flashPromotionSessionId: this.listQuery.flashPromotionSessionId
          });
        }
        this.$confirm('使用要进行添加操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          console.log(selectProducts)
          //return;
          createFlashProductRelation(selectProducts).then(response => {
            if (response.data.code === 0) {
              this.selectDialogVisible = false;
              this.dialogData.multipleSelection = [];
              this.getList();
              this.$message({
                type: 'success',
                message: '添加成功!'
              });
            } else {
              this.$message.error(response.data.msg)
            }

          });
        });
      },
      handleEditDialogConfirm() {
        this.$confirm('是否要确认?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.flashProductRelation.startDate = formatDate(this.miaoshaTime[0], 'yyyy-MM-dd hh:mm:ss');
          this.flashProductRelation.endDate = formatDate(this.miaoshaTime[1], 'yyyy-MM-dd hh:mm:ss');
          console.log(this.flashProductRelation);
          //retuen;
          updateFlashProductRelation(this.flashProductRelation.id, this.flashProductRelation).then(response => {
            if (response.data.code === 0) {
              this.$message({
                message: '修改成功！',
                type: 'success'
              });
              this.editDialogVisible = false;
              this.getList();
            } else {
              this.$message.error(response.data.msg)
            }
          })
        })
      },
      //获取列表
      getList() {
        this.listLoading = true;
        fetchList(this.listQuery).then(response => {
          this.listLoading = false;
          if (response.data.code === 0) {
            let data = response.data.data
            this.list = data.goodsList
            this.total = data.goodsList.length
          }
        });
      },
      getDialogList() {
        fetchProductList(this.dialogData.listQuery).then(response => {
          this.dialogData.list = response.data.data.goodsList;
          this.dialogData.total = response.data.data.goodsList.length;
        })
      }
    }
  }
</script>
<style scoped>
  .operate-container {
    margin-top: 0;
  }

  .input-width {
    width: 200px;
  }
</style>
