<template>
  <div style="margin-top: 50px">
    <el-form :model="value" :rules="rules" ref="productInfoForm" label-width="120px" style="width: 600px" size="small">
      <!--
      <el-form-item label="商品分类：" prop="productCategoryId">
        <el-cascader
          v-model="selectProductCateValue"
          :options="productCateOptions">
        </el-cascader>
      </el-form-item>
      -->
      <el-form-item label="商品名称：" prop="goodsName">
        <el-input v-model="value.goodsName"></el-input>
      </el-form-item>
      <el-form-item label="副标题：" prop="goodsTitle">
        <el-input v-model="value.goodsTitle"></el-input>
      </el-form-item>
      <el-form-item label="商品介绍：">
        <el-input
          :autoSize="true"
          v-model="value.goodsDetail"
          type="textarea"
          placeholder="请输入内容"></el-input>
      </el-form-item>
      <el-form-item label="商品货号：">
        <el-input  :disabled="true" v-model="value.id"></el-input>
      </el-form-item>
      <el-form-item label="商品售价：">
        <el-input v-model="value.goodsPrice"></el-input>
      </el-form-item>
      <el-form-item label="商品库存：">
        <el-input v-model="value.goodsStock"></el-input>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button type="primary" size="medium" @click="handleNext('productInfoForm')">下一步，填写商品属性</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  name: 'ProductInfoDetail',
  props: {
    value: Object,
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      hasEditCreated: false,
      // 选中商品分类的值
      // selectProductCateValue: [],
      rules: {
        goodsName: [
          {required: true, message: '请输入商品名称', trigger: 'blur'},
          {min: 2, max: 140, message: '长度在 2 到 140 个字符', trigger: 'blur'}
        ],
        goodsTitle: [{required: true, message: '请输入商品副标题', trigger: 'blur'}],
        goodsDetail: [{required: true, message: '请输入商品介绍', trigger: 'blur'}]
      }
    }
  },
  created () {
    // console.log(value)
    // this.getProductCateList();
  },
  computed: {
    // 商品的编号
    productId () {
      return this.value.id
    }
  },
  watch: {
    productId: function (newValue) {
      if (!this.isEdit) return
      if (this.hasEditCreated) return
      if (newValue === undefined || newValue == null || newValue === 0) return
      // this.handleEditCreated();
    }
  },
  methods: {
    // 处理编辑逻辑
    handleNext (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$emit('nextStep')
        } else {
          this.$message({
            message: '验证失败',
            type: 'error',
            duration: 1000
          })
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
