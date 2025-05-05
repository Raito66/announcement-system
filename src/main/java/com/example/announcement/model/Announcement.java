package com.example.announcement.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Announcement 實體類，用於描述公告信息。
 * 該類被註解為 JPA 實體，對應數據庫中的 announcement 表。
 */
@Entity // 指定該類為 JPA 實體，對應數據庫表
@Table(name = "announcement") // 指定數據庫表名為 announcement
@Data // Lombok 註解，生成 getter、setter、toString、equals 和 hashCode 方法
@NoArgsConstructor // Lombok 註解，生成無參構造函數
@AllArgsConstructor // Lombok 註解，生成全參構造函數
public class Announcement {

    @Id // 指定該字段為主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵生成策略，使用數據庫自增
    private Integer id; // 公告 ID，唯一標識

    private String title; // 公告標題

    private LocalDate publishDate; // 公告發布日期

    private LocalDate endDate; // 公告截止日期

    @Lob
    private String content; // 公告內容

    private String createdBy; // 公告創建者，用於記錄創建公告的用戶
    
    private String uploadFile1; // 附件1路径
}