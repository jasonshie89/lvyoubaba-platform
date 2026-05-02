package org.example.apimanage.utils;

import io.minio.*;
import io.minio.http.Method;
import org.example.apimanage.config.MinioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 文件上传工具类
 */
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 检查存储桶是否存在，不存在则创建
     */
    public void ensureBucketExists() throws Exception {
        String bucketName = minioConfig.getBucketName();
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件
     * @param file 文件
     * @param folder 文件夹路径（如 images/avatar/）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        ensureBucketExists();

        String bucketName = minioConfig.getBucketName();
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        String objectName = folder + UUID.randomUUID().toString().replace("-", "") + extension;

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // 返回文件访问URL
        return getFileUrl(objectName);
    }

    /**
     * 上传图片（限制类型）
     * @param file 图片文件
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    public String uploadImage(MultipartFile file, String folder) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
        return uploadFile(file, folder);
    }

    /**
     * 获取文件访问URL
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getFileUrl(String objectName) throws Exception {
        String bucketName = minioConfig.getBucketName();
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );
    }

    /**
     * 获取永久访问URL（需要配置公开访问）
     * @param objectName 对象名称
     * @return 永久访问URL
     */
    public String getPermanentUrl(String objectName) {
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName;
    }

    /**
     * 删除文件
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) throws Exception {
        String bucketName = minioConfig.getBucketName();
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 从URL中提取对象名称
     * @param url 文件URL
     * @return 对象名称
     */
    public String extractObjectName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String bucketName = minioConfig.getBucketName();
        int bucketIndex = url.indexOf(bucketName);
        if (bucketIndex != -1) {
            return url.substring(bucketIndex + bucketName.length() + 1);
        }
        return null;
    }
}