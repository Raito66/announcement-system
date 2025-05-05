package com.example.announcement.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	 * @param pageSize   每頁顯示的記錄數
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

	/**
	 * 保存公告與附件的主業務邏輯
	 * 
	 * @param announcement 公告對象
	 * @param uploadFile1  上傳的附件（單一檔案）
	 * @throws IOException 如果保存過程中發生錯誤
	 */
	@Transactional
	public void saveAnnouncementWithAttachments(Announcement announcement, MultipartFile uploadFile1)
			throws IOException {
		try {
			// 處理公告內容中的 Base64 圖片
			String processedContent = processContent(announcement.getContent());
			announcement.setContent(processedContent);

			// 保存附件並獲取保存路徑
			String savedFilePath = null;
			if (uploadFile1 != null && !uploadFile1.isEmpty()) {
				savedFilePath = saveUploadFile(uploadFile1);
				announcement.setUploadFile1(savedFilePath); // 設置附件路徑到公告對象
			}

			// 保存公告到資料庫
			announcementDAO.saveOrUpdate(announcement);
			System.out.println("公告已保存：" + announcement);

			// 如果需要，可以記錄附件保存的路徑
			if (savedFilePath != null) {
				System.out.println("已保存的附件：" + savedFilePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("保存公告或附件失敗: " + e.getMessage());
		}
	}

	/**
	 * 處理公告內容中的 Base64 圖片，並清理不安全的 HTML
	 * 
	 * @param content 公告內容
	 * @return 清理後的安全內容
	 * @throws Exception 如果處理失敗
	 */
	private String processContent(String content) throws Exception {
		if (content == null || content.isEmpty()) {
			return content; // 如果內容為空，直接返回
		}

		// 使用 Jsoup 清理 HTML，避免 XSS 攻擊
		// 定義允許的標籤與屬性
		Safelist safelist = Safelist.relaxed().addTags("p", "a", "strong", "em", "ul", "ol", "li", "span", "div", "img") // 允許的標籤
				.addAttributes("a", "href") // 允許 a 標籤的 href 屬性
				.addAttributes("img", "src", "alt", "title") // 允許 img 標籤的 src、alt、title 屬性
				.addProtocols("img", "src", "http", "https", "data"); // 限制 img 的 src 屬性為 http、https 或 data

		// 清理 HTML，移除不安全的標籤與屬性
		return Jsoup.clean(content, safelist);
	}
	
	/**
     * 保存上傳的文件到指定的目錄，並返回保存的文件名稱
     *
     * @param uploadFile1 上傳的文件
     * @return 保存的文件名稱（包含時間戳）
     * @throws IOException 如果文件保存過程中發生錯誤
     */
    public String saveUploadFile(MultipartFile uploadFile1) throws IOException {
        // 獲取當前操作系統的文件保存目錄
        String uploadDir = getUploadDirectory();

        // 確保目標目錄存在，如果不存在，則自動創建
        Files.createDirectories(Paths.get(uploadDir)); // 如果目錄已存在，則不會執行任何操作

        // 提取原始文件名稱，並檢查其是否有效
        String originalFileName = uploadFile1.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("上傳的文件名稱為空，無法保存");
        }

        // 為文件生成唯一名稱（時間戳 + 原始文件名稱）
        String fileName = System.currentTimeMillis() + "_" + originalFileName;

        // 定義文件的完整保存路徑
        Path destination = Paths.get(uploadDir, fileName);

        // 將上傳的文件保存到目標路徑
        uploadFile1.transferTo(destination.toFile());

        // 返回保存的文件名稱（僅文件名，而非完整路徑）
        return fileName;
    }

    /**
     * 根據操作系統動態獲取文件保存目錄
     *
     * @return 文件保存的目錄路徑
     */
    private String getUploadDirectory() throws IOException {
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

	@Override
	@Transactional
	public void deleteAnnouncement(int id) {
		announcementDAO.delete(id);
	}

}