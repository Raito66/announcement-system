package com.example.announcement.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {

    private Integer id; // 公告 ID

    @NotNull(message = "標題不能為空")
    @Size(max = 100, message = "標題不能超過 100 個字元")
    private String title; // 公告標題

    @NotNull(message = "發布日期不能為空")
    private LocalDate publishDate; // 公告發布日期

    @NotNull(message = "截止日期不能為空")
    private LocalDate endDate; // 公告截止日期

    private String content; // 公告內容（不需要 @Lob）

    @NotNull(message = "創建者不能為空")
    private String createdBy; // 公告創建者

    private MultipartFile uploadFile1; // 附件文件
}