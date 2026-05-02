<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1 class="page-title">约伴管理</h1>
        <p class="page-desc">管理平台中的所有约伴活动</p>
      </div>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增约伴
      </el-button>
    </div>

    <el-card class="content-card" shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索目的地/标题"
              clearable
              prefix-icon="Search"
              class="search-input"
            />
          </el-form-item>
          <el-form-item>
            <el-select v-model="searchForm.status" placeholder="状态" clearable class="select-sm">
              <el-option label="招募中" :value="1" />
              <el-option label="进行中" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="0" />
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
        <el-table-column label="封面" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 6px"
            />
            <span v-else class="no-image">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="destination" label="目的地" width="120" />
        <el-table-column prop="startTime" label="出发日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="天数" width="80" align="center">
          <template #default="{ row }">{{ row.duration || 0 }}天</template>
        </el-table-column>
        <el-table-column prop="budget" label="预算" width="100" align="center">
          <template #default="{ row }">
            <span class="budget-tag">¥{{ row.budget || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="currentMembers" label="人数" width="80" align="center">
          <template #default="{ row }">
            {{ row.currentMembers || 1 }}/{{ row.maxMembers || 10 }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入约伴标题" />
        </el-form-item>
        <el-form-item label="目的地" prop="destination">
          <el-input v-model="formData.destination" placeholder="请输入目的地" />
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
          <div class="upload-tip">建议尺寸：400x300，支持 jpg、png 格式</div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出发日期" prop="startTime">
              <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择日期时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计天数" prop="duration">
              <el-input-number v-model="formData.duration" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预算" prop="budget">
              <el-input-number v-model="formData.budget" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大人数" prop="maxMembers">
              <el-input-number v-model="formData.maxMembers" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">招募中</el-radio>
            <el-radio :label="2">进行中</el-radio>
            <el-radio :label="3">已完成</el-radio>
            <el-radio :label="0">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="formData.contactInfo" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="描述" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="4" placeholder="请输入约伴描述" />
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
import { Plus } from '@element-plus/icons-vue'
import { getTeamList, createTeam, updateTeam, deleteTeam } from '../api/team'

interface Team {
  id?: number
  title: string
  destination: string
  startTime: string
  duration: number
  budget: number
  status: number
  contactInfo: string
  content: string
  coverImage?: string
  maxMembers: number
  currentMembers: number
  createTime?: string
}

// 上传地址
const uploadUrl = computed(() => 'http://localhost:8080/api/file/upload/image')

const searchForm = reactive({
  keyword: '',
  status: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<Team[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)

const formRef = ref<FormInstance>()
const formData = reactive<Team>({
  title: '',
  destination: '',
  startTime: '',
  duration: 1,
  budget: 0,
  status: 1,
  contactInfo: '',
  content: '',
  coverImage: '',
  maxMembers: 10,
  currentMembers: 1
})

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择出发日期', trigger: 'change' }]
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'info', 0: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 1: '招募中', 2: '进行中', 3: '已完成', 0: '已取消' }
  return map[status] || '未知'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
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
      status: searchForm.status
    }
    const result = await getTeamList(params) as any
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
  searchForm.status = null
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增约伴'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: Team) => {
  dialogTitle.value = '编辑约伴'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row: Team) => {
  ElMessageBox.confirm(`确定要删除约伴「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTeam(row.id!)
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
        await updateTeam(formData)
        ElMessage.success('更新成功')
      } else {
        await createTeam(formData)
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
  formData.title = ''
  formData.destination = ''
  formData.startTime = ''
  formData.duration = 1
  formData.budget = 0
  formData.status = 1
  formData.contactInfo = ''
  formData.content = ''
  formData.coverImage = ''
  formData.maxMembers = 10
  formData.currentMembers = 1
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin: 0 0 4px 0; }
.page-desc { font-size: 14px; color: #909399; margin: 0; }
.add-btn { border-radius: 6px; padding: 10px 20px; font-weight: 500; }
.content-card { border-radius: 8px; border: 1px solid #e4e7ed; }
.search-bar { padding: 20px; background: #f5f7fa; border-radius: 6px; margin-bottom: 20px; }
.search-form { display: flex; align-items: center; gap: 16px; margin: 0; }
.search-input { width: 200px; }
.select-sm { width: 130px; }
:deep(.search-input .el-input__wrapper), :deep(.select-sm .el-input__wrapper) { border-radius: 6px; box-shadow: 0 0 0 1px #dcdfe6 inset; }
:deep(.search-input .el-input__wrapper.is-focus), :deep(.select-sm .el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #409eff inset; }
.data-table { margin: 0; }
:deep(.data-table .el-table__header th) { background: #f5f7fa !important; color: #606266; font-weight: 600; }
:deep(.data-table .el-table__body tr:hover > td) { background: #ecf5ff !important; }
.no-image { color: #c0c4cc; font-size: 14px; }
.budget-tag { font-weight: 600; color: #409eff; }
.pagination-wrap { padding: 20px; border-top: 1px solid #e4e7ed; display: flex; justify-content: flex-end; }
:deep(.el-dialog) { border-radius: 8px; }
:deep(.el-dialog__header) { padding: 20px 20px 16px; border-bottom: 1px solid #e4e7ed; }
:deep(.el-dialog__title) { font-size: 16px; font-weight: 600; color: #303133; }
:deep(.el-dialog__body) { padding: 24px 20px; }
:deep(.el-dialog__footer) { padding: 16px 20px; border-top: 1px solid #e4e7ed; }

/* 封面上传样式 */
.cover-uploader { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; transition: border-color 0.3s; }
.cover-uploader:hover { border-color: #409eff; }
.cover-uploader-icon { font-size: 28px; color: #8c939d; width: 180px; height: 100px; text-align: center; line-height: 100px; }
.cover-image { width: 180px; height: 100px; display: block; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 8px; }
</style>