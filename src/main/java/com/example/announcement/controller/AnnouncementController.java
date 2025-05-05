package com.example.announcement.controller;

import java.io.File;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.announcement.dto.AnnouncementDTO;
import com.example.announcement.model.Announcement;
import com.example.announcement.service.AnnouncementService;

/**
 * 公告控制器
 */
@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

	@Autowired
	private AnnouncementService service;

	/**
     * 瀏覽公告列表（分頁）
     *
     * @param page 當前頁碼（默認為 0）
     * @param size 每頁顯示的記錄數（默認為 5）
     * @param model 模型數據
     * @return 公告列表頁面
     */
    @GetMapping
    public String listAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // 獲取公告數據
        List<Announcement> announcements = service.getPagedAnnouncements(page, size);
        Long totalItems = service.getTotalAnnouncementsCount();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        // 添加數據到模型
        model.addAttribute("announcements", announcements);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "announcement/list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("announcement", new AnnouncementDTO()); // 添加模型屬性
        return "announcement/createForm"; // 返回表單頁面
    }
    /**
     * 保存公告
     * @param announcementDTO
     * @param result
     * @param model
     * @return
     */
	@PostMapping("/save-announcement")
	public String saveAnnouncement(
	        @Valid @ModelAttribute("announcement") AnnouncementDTO announcementDTO, // 使用 @Valid 進行校驗
	        BindingResult result, // 用於捕捉校驗結果
	        Model model) {

	    // 1. 檢查校驗是否失敗
	    if (result.hasErrors()) {
	        // 如果校驗失敗，返回表單頁面並顯示錯誤信息
	        model.addAttribute("errors", result.getAllErrors());
	        return "announcement/createForm"; // 回到表單頁面
	    }

	    try {
	        // 2. 將 DTO 轉換為實體類
	        Announcement announcement = convertToEntity(announcementDTO);

	        // 3. 保存公告和附件
	        service.saveAnnouncementWithAttachments(announcement, announcementDTO.getUploadFile1());

	        // 4. 成功消息
	        model.addAttribute("message", "公告與附件保存成功！");
	    } catch (Exception e) {
	        // 5. 處理保存失敗的情況
	        model.addAttribute("message", "保存失敗：" + e.getMessage());
	        return "announcement/createForm"; // 返回表單頁面
	    }

	    return "redirect:/announcements"; // 成功後重定向到公告列表
	}

	/**
	 * 顯示編輯公告的表單
	 *
	 * @param id    公告 ID
	 * @param model 模型數據
	 * @return 編輯表單頁面
	 */
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable int id, Model model) {
	    Announcement announcement = service.getAnnouncementById(id);

	    // 初始化檔案名稱為預設值
	    String fileName = "尚未上傳附件";

	    // 如果有上傳文件，提取檔案名稱並處理
	    if (announcement.getUploadFile1() != null && !announcement.getUploadFile1().isEmpty()) {
	        String rawFileName = new File(announcement.getUploadFile1()).getName(); // 提取純檔案名稱

	        // 找到第一個底線的位置，並去掉之前的部分（包含底線）
	        int underscoreIndex = rawFileName.indexOf('_');
	        if (underscoreIndex != -1) {
	            fileName = rawFileName.substring(underscoreIndex + 1); // 去掉第一個底線之前的部分
	        } else {
	            fileName = rawFileName; // 如果沒有底線，直接使用原始檔案名稱
	        }
	    }

	    // 將檔案名稱和公告對象添加到模型中
	    model.addAttribute("fileName", fileName);
	    model.addAttribute("announcement", announcement);

	    // 返回對應的模板名稱
	    return "announcement/updateForm";
	}

	/**
	 * 更新公告
	 *
	 * @param id              公告 ID
	 * @param announcementDTO 公告 DTO 對象
	 * @param result          校驗結果
	 * @param model           模型數據
	 * @return 重定向到公告列表頁面或返回表單頁面
	 */
	@PostMapping("/update/{id}")
	public String updateAnnouncement(
	        @PathVariable int id,
	        @Valid @ModelAttribute("announcement") AnnouncementDTO announcementDTO, // 使用 DTO 並進行校驗
	        BindingResult result,
	        Model model) {

	    // 1. 檢查校驗是否失敗
	    if (result.hasErrors()) {
	        // 如果校驗失敗，返回表單頁面並顯示錯誤信息
	        model.addAttribute("errors", result.getAllErrors());
	        return "announcement/updateForm"; // 返回到更新表單頁面
	    }

	    try {
	        // 2. 將 DTO 轉換為實體類
	        Announcement announcement = convertToEntity(announcementDTO);

	        // 3. 設置公告 ID
	        announcement.setId(id);

	        // 4. 更新公告與附件
	        service.saveAnnouncementWithAttachments(announcement, announcementDTO.getUploadFile1());

	        // 5. 成功消息
	        model.addAttribute("message", "公告與附件更新成功！");
	    } catch (Exception e) {
	        // 6. 處理保存失敗的情況
	        model.addAttribute("message", "更新失敗：" + e.getMessage());
	        return "announcement/updateForm"; // 返回到更新表單頁面
	    }

	    return "redirect:/announcements"; // 成功後重定向到公告列表
	}

	/**
	 * 刪除公告
	 *
	 * @param id 公告 ID
	 * @return 重定向到公告列表頁面
	 */
	@GetMapping("/delete/{id}")
	public String deleteAnnouncement(@PathVariable int id) {
		service.deleteAnnouncement(id);
		return "redirect:/announcements";
	}

	/**
	 * 將 AnnouncementDTO 轉換為 Announcement 實體類
	 *
	 * @param dto 公告 DTO 對象
	 * @return Announcement 實體類對象
	 */
	private Announcement convertToEntity(AnnouncementDTO dto) {
	    Announcement announcement = new Announcement();
	    announcement.setId(dto.getId());
	    announcement.setTitle(dto.getTitle());
	    announcement.setPublishDate(dto.getPublishDate());
	    announcement.setEndDate(dto.getEndDate());
	    announcement.setContent(dto.getContent());
	    announcement.setCreatedBy(dto.getCreatedBy());
	    return announcement;
	}
}