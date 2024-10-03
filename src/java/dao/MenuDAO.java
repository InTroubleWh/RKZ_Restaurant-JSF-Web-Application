package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import java.util.List;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import util.HibernateUtil;
import model.Menu;

@Named(value = "menuDAO")
@ApplicationScoped
public class MenuDAO implements Serializable {

    private List<Menu> menuItems;

    public MenuDAO() {
    }

    public List<Menu> allMenuItems() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        menuItems = null; //clear list of previous objects.
        try {
            session.beginTransaction();
            menuItems = session.createCriteria(Menu.class).list();
            int count = menuItems.size();
            System.out.println("!--menu list generated: " + count + " --!"); //debugging purpose
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return menuItems;
    }

    public List<Menu> getMenuItemsByCategory(String category) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        menuItems = null; //clear list of previous objects.
        try {
            session.beginTransaction();
            menuItems = session.createCriteria(Menu.class)
                    .add(Restrictions.eq("category", category))
                    .add(Restrictions.eq("active", "active")) //only active menu items
                    .list();
            session.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("Exception caught while getting Menu. Caught Exception: " + ex);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return menuItems;
    }

    public Menu getMenuById(int itemId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Menu menuItem = null;
        try {
            session.beginTransaction();
            menuItem = (Menu) session.get(Menu.class, itemId); // Retrieve the Menu object by itemId.
            session.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("Exception caught while getting Menu by ID. Caught Exception: " + ex);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return menuItem;
    }

    public void addMenu(Menu menu) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(menu);
            transaction.commit();
        } catch (Exception ex) {
            System.out.println("Exception while registering user account = " + ex);
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void saveMenu(Menu menu) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(menu);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteMenu(Menu menu) {
        Transaction transaction = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession(); // Buka sesi Hibernate
            transaction = session.beginTransaction(); // Mulai transaksi
            session.delete(menu); // Hapus entitas UserAccounts
            transaction.commit(); // Komit transaksi
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback jika terjadi error
            }
            e.printStackTrace(); // Cetak stack trace
            throw e; // Lempar ulang exception agar dapat ditangani di UserSessionBean
        } finally {
            if (session != null) {
                session.close(); // Tutup sesi Hibernate
            }
        }
    }
}
