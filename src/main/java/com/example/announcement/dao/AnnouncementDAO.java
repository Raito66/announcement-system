package com.example.announcement.dao;

import com.example.announcement.model.Announcement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AnnouncementDAO 負責與數據庫進行交互。
 */
@Repository
public class AnnouncementDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 獲取當前的 Hibernate Session
     *
     * @return Hibernate Session
     */
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 獲取所有公告（分頁）
     *
     * @param pageNumber 當前頁碼（從 0 開始）
     * @param pageSize 每頁顯示的記錄數
     * @return 分頁的公告列表
     */
    public List<Announcement> getPagedAnnouncements(int pageNumber, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Announcement ORDER BY publishDate DESC", Announcement.class)
                .setFirstResult(pageNumber * pageSize) // 起始記錄索引
                .setMaxResults(pageSize)              // 每頁記錄數
                .list();
    }

    /**
     * 獲取公告的總數量
     *
     * @return 公告總數量
     */
    public Long getTotalAnnouncementsCount() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT COUNT(a) FROM Announcement a", Long.class)
                .uniqueResult();
    }

    /**
     * 根據 ID 獲取公告
     *
     * @param id 公告 ID
     * @return 公告對象
     */
    public Announcement getById(int id) {
        return getCurrentSession().get(Announcement.class, id);
    }

    /**
     * 保存或更新公告
     *
     * @param announcement 公告對象
     */
    public void saveOrUpdate(Announcement announcement) {
        getCurrentSession().saveOrUpdate(announcement);
    }

    /**
     * 刪除公告
     *
     * @param id 公告 ID
     */
    public void delete(int id) {
        Announcement announcement = getById(id);
        if (announcement != null) {
            getCurrentSession().delete(announcement);
        }
    }
}