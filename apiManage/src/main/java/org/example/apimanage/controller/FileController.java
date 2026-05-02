package org.example.apimanage.controller;

import org.example.apimanage.dto.R;
import org.example.apimanage.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传 Controller
 */
@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 上传单个图片
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/image")
    public R<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = minioUtil.uploadImage(file, "images/");
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return R.ok(result);
        } catch (IllegalArgumentException e) {
            return R.error("只能上传图片文件");
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传多个图片
     * @param files 图片文件列表
     * @return 图片URL列表
     */
    @PostMapping("/upload/images")
    public R<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = minioUtil.uploadImage(file, "images/");
                urls.add(url);
            }
            return R.ok(urls);
        } catch (IllegalArgumentException e) {
            return R.error("只能上传图片文件");
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/avatar")
    public R<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = minioUtil.uploadImage(file, "images/avatar/");
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return R.ok(result);
        } catch (IllegalArgumentException e) {
            return R.error("只能上传图片文件");
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传景点封面图
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/scenic")
    public R<Map<String, String>> uploadScenicImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = minioUtil.uploadImage(file, "images/scenic/");
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return R.ok(result);
        } catch (IllegalArgumentException e) {
            return R.error("只能上传图片文件");
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传动态图片
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/activity")
    public R<Map<String, String>> uploadActivityImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = minioUtil.uploadImage(file, "images/activity/");
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return R.ok(result);
        } catch (IllegalArgumentException e) {
            return R.error("只能上传图片文件");
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param url 文件URL
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public R<Boolean> deleteFile(@RequestParam("url") String url) {
        try {
            String objectName = minioUtil.extractObjectName(url);
            if (objectName != null) {
                minioUtil.deleteFile(objectName);
                return R.ok(true);
            }
            return R.error("无效的文件URL");
        } catch (Exception e) {
            return R.error("删除失败: " + e.getMessage());
        }
    }
}