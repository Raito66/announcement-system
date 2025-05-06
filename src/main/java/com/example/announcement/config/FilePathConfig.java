package com.example.announcement.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 表示這是一個配置類，等同於 Spring 的 XML 配置文件。Spring 容器會在啟動時加載這個類，並應用其中的配置。
public class FilePathConfig {

    @Bean
    public String uploadDirectory() throws IOException {
        // 獲取操作系統名稱
        String os = System.getProperty("os.name").toLowerCase();

        // 根據操作系統返回不同的文件保存路徑
        String uploadDir;
        if (os.contains("win")) {
            uploadDir = "D:/announcement-system/uploads";
        } else if (os.contains("linux")) {
            uploadDir = "/var/announcement-system/uploads";
        } else {
            throw new UnsupportedOperationException("不支持的操作系統: " + os);
        }

        // 檢查目標目錄是否可寫
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath); // 嘗試創建目錄
            } catch (IOException e) {
                throw new IOException("無法創建目錄，請檢查權限: " + uploadPath, e);
            }
        }

        if (!Files.isWritable(uploadPath)) {
            throw new IOException("目錄不可寫，請檢查權限: " + uploadPath);
        }

        return uploadDir;
    }
}