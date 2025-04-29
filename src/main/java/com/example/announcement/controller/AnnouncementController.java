package com.example.announcement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		model.addAttribute("announcement", new Announcement());
		return "announcement/createForm";
	}

	@PostMapping("/save-announcement")
	public String saveAnnouncement(@ModelAttribute Announcement announcement) {
		service.saveAnnouncement(announcement);
		return "redirect:/announcements";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable int id, Model model) {
		model.addAttribute("announcement", service.getAnnouncementById(id));
		return "announcement/updateForm";
	}

	@PostMapping("/update/{id}")
	public String updateAnnouncement(@PathVariable int id, @ModelAttribute Announcement announcement) {
		announcement.setId(id);
		service.saveAnnouncement(announcement);
		return "redirect:/announcements";
	}

	@GetMapping("/delete/{id}")
	public String deleteAnnouncement(@PathVariable int id) {
		service.deleteAnnouncement(id);
		return "redirect:/announcements";
	}
}