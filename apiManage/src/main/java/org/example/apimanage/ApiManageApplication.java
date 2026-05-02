package org.example.apimanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 驴友 APP 平台后端启动类
 */
@SpringBootApplication
@MapperScan("org.example.apimanage.mapper")
public class ApiManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiManageApplication.class, args);
        System.out.println("=================================");
        System.out.println("驴友 APP 平台后端启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("=================================");
    }

}