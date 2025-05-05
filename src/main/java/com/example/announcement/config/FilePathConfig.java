package com.example.announcement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilePathConfig {

    @Bean
    public String uploadDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows 系統的文件路徑
            return "D:/announcement-system/uploads";
        } else if (os.contains("linux")) {
            // Linux 系統的文件路徑
            return "/var/announcement-system/uploads";
        } else {
            throw new UnsupportedOperationException("不支持的操作系統: " + os);
        }
    }
}