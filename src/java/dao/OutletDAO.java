package dao;

import java.util.List;
import org.hibernate.Session;
import model.Outlet;
import util.HibernateUtil;

public class OutletDAO {

    public OutletDAO() {
    }

    public List<Outlet> allOutlets() {
        List<Outlet> outlets = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();
            outlets = session.createCriteria(Outlet.class).list();
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        return outlets;
    }

    public Outlet findById(int id) {
        Session session = null;
        Outlet ot = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();
            ot = (Outlet) session.get(Outlet.class, id);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        return ot;
    }
}
