package com.example.announcement.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.announcement.dao.AnnouncementDAO;
import com.example.announcement.model.Announcement;
import com.example.announcement.service.AnnouncementService;


/**
 * AnnouncementServiceImpl 負責實現公告相關的業務邏輯。
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementDAO announcementDAO;
    
    /**
     * 獲取公告列表（分頁）
     *
     * @param pageNumber 當前頁碼（從 0 開始）
     * @param pageSize 每頁顯示的記錄數
     * @return 分頁的公告列表
     */
    @Transactional
    public List<Announcement> getPagedAnnouncements(int pageNumber, int pageSize) {
        return announcementDAO.getPagedAnnouncements(pageNumber, pageSize);
    }

    /**
     * 獲取公告總數量
     *
     * @return 公告總數量
     */
    @Transactional
    public Long getTotalAnnouncementsCount() {
        return announcementDAO.getTotalAnnouncementsCount();
    }

    @Override
    @Transactional
    public Announcement getAnnouncementById(int id) {
        return announcementDAO.getById(id);
    }

    @Override
    @Transactional
    public void saveAnnouncement(Announcement announcement) {
        try {
            // 處理公告內容中的 Base64 圖片
            String processedContent = processContent(announcement.getContent());
            announcement.setContent(processedContent);

            // 保存公告到資料庫
            announcementDAO.saveOrUpdate(announcement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("公告保存失敗: " + e.getMessage());
        }
    }

    private String processContent(String content) throws Exception {
        if (content == null || content.isEmpty()) {
            return content; // 如果內容為空，直接返回
        }

        // 使用 Jsoup 清理 HTML，避免 XSS 攻擊
        // 定義允許的標籤與屬性
        Safelist safelist = Safelist.relaxed()
            .addTags("p", "a", "strong", "em", "ul", "ol", "li", "span", "div", "img") // 允許的標籤
            .addAttributes("a", "href") // 允許 a 標籤的 href 屬性
            .addAttributes("img", "src", "alt", "title") // 允許 img 標籤的 src、alt、title 屬性
            .addProtocols("img", "src", "http", "https", "data"); // 限制 img 的 src 屬性為 http、https 或 data

        // 清理 HTML，移除不安全的標籤與屬性
        String safeContent = Jsoup.clean(content, safelist);

        // 返回清理後的 HTML
        return safeContent;
    }

    @Override
    @Transactional
    public void deleteAnnouncement(int id) {
        announcementDAO.delete(id);
    }
}