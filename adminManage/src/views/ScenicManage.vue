<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1 class="page-title">景点管理</h1>
        <p class="page-desc">管理平台中的所有景点信息</p>
      </div>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增景点
      </el-button>
    </div>

    <el-card class="content-card" shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索景点名称"
              clearable
              prefix-icon="Search"
              class="search-input"
            />
          </el-form-item>
          <el-form-item>
            <el-select v-model="searchForm.isHot" placeholder="是否热门" clearable class="select-sm">
              <el-option label="是" :value="1" />
              <el-option label="否" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border v-loading="loading" class="data-table">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="景点图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.coverImage || `https://picsum.photos/seed/scenic${row.id}/100/100`"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 6px"
              :preview-src-list="[row.coverImage || 'https://picsum.photos/seed/scenic' + row.id + '/400/300']"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="景点名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="province" label="省份" width="100" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="ticketPrice" label="门票价格" width="120" align="center">
          <template #default="{ row }">
            <span v-if="row.ticketPrice > 0" class="price-tag">¥{{ row.ticketPrice }}</span>
            <span v-else class="free-tag">免费</span>
          </template>
        </el-table-column>
        <el-table-column prop="isHot" label="热门" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isHot === 1" type="danger" size="small">热门</el-tag>
            <span v-else class="normal-tag">普通</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center">
          <template #default="{ row }">
            <span class="view-count">
              <el-icon><View /></el-icon>
              {{ row.viewCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="景点介绍" min-width="200">
          <template #default="{ row }">
            <div class="desc-text-wrap">{{ row.description }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="省份" prop="province">
              <el-input v-model="formData.province" placeholder="请输入省份" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市" prop="city">
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="location">
          <el-input v-model="formData.location" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
            accept="image/*"
          >
            <el-image v-if="formData.coverImage" :src="formData.coverImage" class="cover-image" fit="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸：750x400，支持 jpg、png 格式</div>
        </el-form-item>
        <el-form-item label="门票价格" prop="ticketPrice">
          <el-input-number v-model="formData.ticketPrice" :min="0" :precision="2" class="price-input" />
          <span class="price-unit">元</span>
        </el-form-item>
        <el-form-item label="开放时间" prop="openingHours">
          <el-input v-model="formData.openingHours" placeholder="请输入开放时间" />
        </el-form-item>
        <el-form-item label="最佳季节" prop="bestSeason">
          <el-input v-model="formData.bestSeason" placeholder="请输入最佳游玩季节" />
        </el-form-item>
        <el-form-item label="是否热门" prop="isHot">
          <el-radio-group v-model="formData.isHot">
            <el-radio :label="1">是，设为热门景点</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="景点介绍" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入景点介绍" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadProps } from 'element-plus'
import { Plus, View } from '@element-plus/icons-vue'
import { getScenicSpotList, createScenicSpot, updateScenicSpot, deleteScenicSpot } from '../api/scenicSpot'

interface ScenicSpot {
  id?: number
  name: string
  province?: string
  city?: string
  location?: string
  ticketPrice: number
  isHot: number
  description?: string
  coverImage?: string
  openingHours?: string
  bestSeason?: string
  viewCount?: number
}

// 上传地址
const uploadUrl = computed(() => 'http://localhost:8080/api/file/upload/scenic')

const searchForm = reactive({
  keyword: '',
  isHot: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<ScenicSpot[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)

const formRef = ref<FormInstance>()
const formData = reactive<ScenicSpot>({
  name: '',
  province: '',
  city: '',
  location: '',
  ticketPrice: 0,
  isHot: 0,
  description: '',
  coverImage: '',
  openingHours: '',
  bestSeason: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入景点名称', trigger: 'blur' }
  ]
}

// 封面上传成功
const handleCoverSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    formData.coverImage = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传前校验
const beforeCoverUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword || undefined,
      isHot: searchForm.isHot
    }
    const result = await getScenicSpotList(params) as any
    console.log('API返回结果:', result)
    if (result && result.data) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.isHot = null
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增景点'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: ScenicSpot) => {
  dialogTitle.value = '编辑景点'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row: ScenicSpot) => {
  ElMessageBox.confirm(`确定要删除景点「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteScenicSpot(row.id!)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value) {
        await updateScenicSpot(formData)
        ElMessage.success('更新成功')
      } else {
        await createScenicSpot(formData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
    } finally {
      submitting.value = false
    }
  })
}

const resetForm = () => {
  formData.name = ''
  formData.province = ''
  formData.city = ''
  formData.location = ''
  formData.ticketPrice = 0
  formData.isHot = 0
  formData.description = ''
  formData.coverImage = ''
  formData.openingHours = ''
  formData.bestSeason = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.page-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.add-btn {
  border-radius: 6px;
  padding: 10px 20px;
  font-weight: 500;
}

.content-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.search-bar {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 0;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.select-sm {
  width: 100px;
}

:deep(.search-input .el-input__wrapper),
:deep(.select-sm .el-input__wrapper) {
  border-radius: 6px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.search-input .el-input__wrapper.is-focus),
:deep(.select-sm .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.data-table {
  margin: 0;
}

:deep(.data-table .el-table__header th) {
  background: #f5f7fa !important;
  color: #606266;
  font-weight: 600;
}

:deep(.data-table .el-table__body tr:hover > td) {
  background: #ecf5ff !important;
}

:deep(.data-table .el-table__body td) {
  white-space: normal;
  word-break: break-all;
}

.price-tag {
  font-weight: 600;
  color: #e6a23c;
}

.free-tag {
  font-weight: 500;
  color: #67c23a;
}

.normal-tag {
  font-size: 12px;
  color: #909399;
}

.view-count {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
}

.desc-text-wrap {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-all;
  white-space: normal;
}

.pagination-wrap {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 16px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 24px 20px;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
}

.price-input {
  width: 200px;
}

.price-unit {
  margin-left: 8px;
  color: #909399;
}

/* 封面上传样式 */
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 180px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.cover-image {
  width: 180px;
  height: 100px;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>