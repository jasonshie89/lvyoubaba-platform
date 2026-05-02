<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1 class="page-title">动态管理</h1>
        <p class="page-desc">管理平台中的所有用户动态</p>
      </div>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增动态
      </el-button>
    </div>

    <el-card class="content-card" shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索动态内容"
              clearable
              prefix-icon="Search"
              class="search-input"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border v-loading="loading" class="data-table">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="content" label="动态内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.images"
              :src="parseImages(row.images)[0]"
              :preview-src-list="parseImages(row.images)"
              class="preview-image"
              fit="cover"
            />
            <span v-else class="no-image">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞" width="80" align="center">
          <template #default="{ row }">
            <span class="like-count">
              <el-icon><Star /></el-icon>
              {{ row.likeCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="commentCount" label="评论" width="80" align="center">
          <template #default="{ row }">
            <span class="comment-count">
              <el-icon><ChatDotRound /></el-icon>
              {{ row.commentCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
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
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="formData.userId" :min="1" style="width: 200px" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="5" placeholder="请输入动态内容" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            accept="image/*"
            :limit="9"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="image-tip">支持上传多张图片，最多9张，单张不超过5MB</div>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="formData.tags" placeholder="请输入标签，多个用逗号分隔" />
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
import type { FormInstance, UploadProps, UploadUserFile } from 'element-plus'
import { Plus, Star, ChatDotRound } from '@element-plus/icons-vue'
import { getActivityList, createActivity, updateActivity, deleteActivity } from '../api/activity'

interface Activity {
  id?: number
  userId: number
  content: string
  images: string
  tags?: string
  likeCount?: number
  commentCount?: number
  createTime?: string
}

// 上传地址
const uploadUrl = computed(() => 'http://localhost:8080/api/file/upload/activity')

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<Activity[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)

// 图片文件列表
const fileList = ref<UploadUserFile[]>([])
// 已上传的图片URL列表
const uploadedUrls = ref<string[]>([])

const formRef = ref<FormInstance>()
const formData = reactive<Activity>({
  userId: 1,
  content: '',
  images: '',
  tags: ''
})

const formRules = {
  content: [{ required: true, message: '请输入动态内容', trigger: 'blur' }],
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }]
}

const parseImages = (images: string) => {
  if (!images) return []
  try {
    const parsed = JSON.parse(images)
    return Array.isArray(parsed) ? parsed : images.split(',').filter(Boolean)
  } catch {
    return images.split(',').filter(Boolean)
  }
}

// 上传成功
const handleUploadSuccess: UploadProps['onSuccess'] = (response, file) => {
  if (response.code === 200) {
    uploadedUrls.value.push(response.data.url)
    updateImagesValue()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 删除图片
const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index !== -1 && uploadedUrls.value[index]) {
    uploadedUrls.value.splice(index, 1)
  }
  updateImagesValue()
}

// 更新images值
const updateImagesValue = () => {
  formData.images = uploadedUrls.value.join(',')
}

// 上传前校验
const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
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
      keyword: searchForm.keyword || undefined
    }
    const result = await getActivityList(params) as any
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
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增动态'
  isEdit.value = false
  fileList.value = []
  uploadedUrls.value = []
  dialogVisible.value = true
}

const handleEdit = (row: Activity) => {
  dialogTitle.value = '编辑动态'
  isEdit.value = true
  if (row.id) formData.id = row.id
  formData.userId = row.userId
  formData.content = row.content
  formData.images = row.images
  formData.tags = row.tags || ''

  // 解析已有图片
  const urls = parseImages(row.images)
  uploadedUrls.value = urls
  fileList.value = urls.map((url: string, index: number) => ({
    name: `image-${index}`,
    url: url
  }))

  dialogVisible.value = true
}

const handleDelete = (row: Activity) => {
  ElMessageBox.confirm('确定要删除这条动态吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteActivity(row.id!)
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
        await updateActivity(formData)
        ElMessage.success('更新成功')
      } else {
        await createActivity(formData)
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
  formData.userId = 1
  formData.content = ''
  formData.images = ''
  formData.tags = ''
  fileList.value = []
  uploadedUrls.value = []
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
.search-input { width: 320px; }
:deep(.search-input .el-input__wrapper) { border-radius: 6px; box-shadow: 0 0 0 1px #dcdfe6 inset; }
:deep(.search-input .el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #409eff inset; }
.data-table { margin: 0; }
:deep(.data-table .el-table__header th) { background: #f5f7fa !important; color: #606266; font-weight: 600; }
:deep(.data-table .el-table__body tr:hover > td) { background: #ecf5ff !important; }
.preview-image { width: 56px; height: 56px; border-radius: 6px; cursor: pointer; border: 1px solid #e4e7ed; }
.preview-image:hover { transform: scale(1.1); border-color: #409eff; }
.no-image { font-size: 18px; color: #c0c4cc; }
.like-count, .comment-count { display: inline-flex; align-items: center; gap: 4px; font-size: 13px; color: #909399; }
.like-count { color: #f56c6c; }
.comment-count { color: #67c23a; }
.pagination-wrap { padding: 20px; border-top: 1px solid #e4e7ed; display: flex; justify-content: flex-end; }
:deep(.el-dialog) { border-radius: 8px; }
:deep(.el-dialog__header) { padding: 20px 20px 16px; border-bottom: 1px solid #e4e7ed; }
:deep(.el-dialog__title) { font-size: 16px; font-weight: 600; color: #303133; }
:deep(.el-dialog__body) { padding: 24px 20px; }
:deep(.el-dialog__footer) { padding: 16px 20px; border-top: 1px solid #e4e7ed; }
.image-tip { font-size: 12px; color: #909399; margin-top: 8px; }
</style>