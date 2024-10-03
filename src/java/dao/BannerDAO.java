package dao;

import java.util.List;
import model.Banner;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class BannerDAO {

    public List<Banner> getBanners() {
        List<Banner> banners = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            banners = session.createCriteria(Banner.class).list();
            int count = banners.size();
            System.out.println("Fetched " + count + " banners!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.close();
        }
        return banners;
    }

    public Banner getBannerById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Banner banner = null;
        try {
            session.beginTransaction();
            banner = (Banner) session.get(Banner.class, id); // Retrieve the Menu object by itemId.
            session.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("Exception caught while getting Menu by ID. Caught Exception: " + ex);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return banner;
    }
}
