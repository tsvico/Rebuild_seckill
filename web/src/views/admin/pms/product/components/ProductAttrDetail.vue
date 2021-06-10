<template>
  <div style="margin-top: 50px">
    <el-form :model="value" ref="productAttrForm" label-width="120px" style="width: 720px" size="small">
      <el-form-item label="属性类型：">
        <el-select v-model="value.stockCount>0?2:1"
                   placeholder="请选择属性类型"
                   @change="handleProductAttrChange">
          <el-option
            v-for="item in productAttributeCategoryOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品相册：">
        <el-input
            placeholder="请输入图片地址"
            prefix-icon="el-icon-picture-outline"
            v-model="value.goodsImg" >
          </el-input>
		  <!--
        <multi-upload v-model="selectProductPics"></multi-upload>
		-->
      </el-form-item>
      <el-form-item label="规格参数：">
        <el-tabs v-model="activeHtmlName" type="card">
          <el-tab-pane label="电脑端详情" name="pc">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入内容"
              v-model="value.goodsDetail">
            </el-input>
          </el-tab-pane>
          <el-tab-pane label="移动端详情" name="mobile">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入内容"
              v-model="value.goodsDetail">
            </el-input>
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button size="medium" @click="handlePrev">上一步，填写商品信息</el-button>
        <el-button type="primary" size="medium" @click="handleFinishCommit">完成，提交商品</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {fetchList as fetchProductAttrList} from '@/utils/api/productAttr'
import axios from 'axios'

export default {
  name: 'ProductAttrDetail',
  props: {
    value: Object,
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      // 编辑模式时是否初始化成功
      hasEditCreated: false,
      // 商品属性分类下拉选项
      productAttributeCategoryOptions: [],
      // 选中的商品参数
      selectProductParam: [],
      // 可手动添加的商品属性
      addProductAttrValue: '',
      // 商品富文本详情激活类型
      activeHtmlName: 'pc'
    }
  },
  computed: {
    // 商品的编号
    productId () {
      return this.value.id
    },
    // 商品的主图和画册图片
    selectProductPics: {
      get: function () {
        let pics = []
        if (this.value.pic === undefined || this.value.pic == null || this.value.pic === '') {
          return pics
        }
        pics.push(this.value.pic)
        if (this.value.albumPics === undefined || this.value.albumPics == null || this.value.albumPics === '') {
          return pics
        }
        let albumPics = this.value.albumPics.split(',')
        for (let i = 0; i < albumPics.length; i++) {
          pics.push(albumPics[i])
        }
        return pics
      },
      set: function (newValue) {
        if (newValue == null || newValue.length === 0) {
          this.value.pic = null
          this.value.albumPics = null
        } else {
          this.value.pic = newValue[0]
          this.value.albumPics = ''
          if (newValue.length > 1) {
            for (let i = 1; i < newValue.length; i++) {
              this.value.albumPics += newValue[i]
              if (i !== newValue.length - 1) {
                this.value.albumPics += ','
              }
            }
          }
        }
      }
    }
  },
  created () {
    this.getProductAttrCateList()
  },
  watch: {
    productId: function (newValue) {
      if (!this.isEdit) return
      if (this.hasEditCreated) return
      if (newValue === undefined || newValue == null || newValue === 0) return
      this.handleEditCreated()
    }
  },
  methods: {
    handleEditCreated () {
      // 根据商品属性分类id获取属性和参数
      if (this.value.productAttributeCategoryId != null) {
        this.handleProductAttrChange(this.value.productAttributeCategoryId)
      }
      this.hasEditCreated = true
    },
    getProductAttrCateList () {
      axios.get('../../static/data/productAttributelist.json').then(response => {
        this.productAttributeCategoryOptions = []
        let list = response.data.data.list
        for (let i = 0; i < list.length; i++) {
          this.productAttributeCategoryOptions.push({label: list[i].name, value: list[i].id})
        }
      })
    },
   // 获取设置的可手动添加属性值
    getEditAttrOptions (id) {
      let options = []
      for (let i = 0; i < this.value.productAttributeValueList.length; i++) {
        let attrValue = this.value.productAttributeValueList[i]
        if (attrValue.productAttributeId === id) {
          let strArr = attrValue.value.split(',')
          for (let j = 0; j < strArr.length; j++) {
            options.push(strArr[j])
          }
          break
        }
      }
      return options
    },
    // 获取选中的属性值
    getEditAttrValues (index) {
      let values = new Set()
      if (index === 0) {
        for (let i = 0; i < this.value.skuStockList.length; i++) {
          let sku = this.value.skuStockList[i]
          let spData = JSON.parse(sku.spData)
          if (spData != null && spData.length >= 1) {
            values.add(spData[0].value)
          }
        }
      } else if (index === 1) {
        for (let i = 0; i < this.value.skuStockList.length; i++) {
          let sku = this.value.skuStockList[i]
          let spData = JSON.parse(sku.spData)
          if (spData != null && spData.length >= 2) {
            values.add(spData[1].value)
          }
        }
      } else {
        for (let i = 0; i < this.value.skuStockList.length; i++) {
          let sku = this.value.skuStockList[i]
          let spData = JSON.parse(sku.spData)
          if (spData != null && spData.length >= 3) {
            values.add(spData[2].value)
          }
        }
      }
      return Array.from(values)
    },
    // 获取属性的值
    getEditParamValue (id) {
      for (let i = 0; i < this.value.productAttributeValueList.length; i++) {
        if (id === this.value.productAttributeValueList[i].productAttributeId) {
          return this.value.productAttributeValueList[i].value
        }
      }
    },
    handleProductAttrChange (value) {
      //this.getProductAttrList(0, value)
      //this.getProductAttrList(1, value)
    },
    getInputListArr (inputList) {
      return inputList.split(',')
    },
    handleAddProductAttrValue (idx) {
      //let options = this.selectProductAttr[idx].options
      if (this.addProductAttrValue == null || this.addProductAttrValue == '') {
        this.$message({
          message: '属性值不能为空',
          type: 'warning',
          duration: 1000
        })
        return
      }
      if (options.indexOf(this.addProductAttrValue) !== -1) {
        this.$message({
          message: '属性值不能重复',
          type: 'warning',
          duration: 1000
        })
        return
      }
      //this.selectProductAttr[idx].options.push(this.addProductAttrValue)
      this.addProductAttrValue = null
    },
    handleRemoveProductAttrValue (idx, index) {
      //this.selectProductAttr[idx].options.splice(index, 1)
    },
    getProductSkuSp (row, index) {
      let spData = JSON.parse(row.spData)
      if (spData != null && index < spData.length) {
        return spData[index].value
      } else {
        return null
      }
    },
    // 合并商品属性图片
    mergeProductAttrPics () {
      console.log("合并商品属性图片,传递value")
      console.log(this.isEdit)
    },
    getOptionStr (arr) {
      let str = ''
      for (let i = 0; i < arr.length; i++) {
        str += arr[i]
        if (i != arr.length - 1) {
          str += ','
        }
      }
      return str
    },
    handleRemoveProductSku (index, row) {
      let list = this.value.skuStockList
      if (list.length === 1) {
        list.pop()
      } else {
        list.splice(index, 1)
      }
    },
    getParamInputList (inputList) {
      return inputList.split(',')
    },
    handlePrev () {
      this.$emit('prevStep')
    },
    handleFinishCommit () {
      this.mergeProductAttrPics()
      this.$emit('finishCommit',this.isEdit);
    }
  }
}
</script>

<style scoped>
  .littleMarginLeft {
    margin-left: 10px;
  }

  .littleMarginTop {
    margin-top: 10px;
  }

  .paramInput {
    width: 250px;
  }

  .paramInputLabel {
    display: inline-block;
    width: 100px;
    text-align: right;
    padding-right: 10px
  }

  .cardBg {
    background: #F8F9FC;
  }
</style>
