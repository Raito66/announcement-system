package com.example.announcement.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.announcement.model.Announcement;

/**
 * AnnouncementService 定義公告相關的業務邏輯接口。
 */
public interface AnnouncementService {
	
	/**
	 * 獲取分頁的公告列表
	 * 
	 * @param pageNumber 當前頁碼（從 0 開始）
	 * @param pageSize   每頁顯示的記錄數
	 * @return 分頁的公告列表
	 */
	List<Announcement> getPagedAnnouncements(int pageNumber, int pageSize);

	/**
	 * 獲取總公告數量
	 * 
	 * @return 公告的總數量
	 */
	Long getTotalAnnouncementsCount();

	/**
	 * 根據 ID 獲取公告
	 *
	 * @param id 公告 ID
	 * @return 公告對象
	 */
	Announcement getAnnouncementById(int id);

	/**
	 * 刪除公告
	 *
	 * @param id 公告 ID
	 */
	void deleteAnnouncement(int id);
	
	/**
	 * 儲存公告
	 *
	 * @param announcement 公告對象
	 */
	void saveAnnouncementWithAttachments(Announcement announcement, MultipartFile uploadFile1) throws IOException;
	
}