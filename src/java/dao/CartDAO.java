package dao;

import org.hibernate.Session;
import org.hibernate.Query;
import javax.inject.Named;
import java.util.List;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import model.Cart;
import model.Menu;
import model.UserAccounts;
import org.hibernate.Transaction;
import util.HibernateUtil;

@Named(value = "cartDAO")
@ApplicationScoped

public class CartDAO implements Serializable {

    public CartDAO() {
    }

    // Find cart item by user and menu
    public Cart findCartByUserAndMenu(UserAccounts user, Menu menu) {
        Session session = null;
        List<Cart> results = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                    "FROM model.Cart c WHERE c.userAccounts.userId = :userId AND c.menu.itemId = :menuId");
            query.setParameter("userId", user.getUserId());
            query.setParameter("menuId", menu.getItemId());
            results = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.close();
        }
        return results.isEmpty() ? null : results.get(0);
    }

    //Method to fetch cart items for the current user
    public List<Cart> getCartItemsByUser(UserAccounts user) {
        Session session = null;
        List<Cart> cartItems = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM model.Cart c WHERE c.userAccounts.userId = :userId");
            query.setParameter("userId", user.getUserId());
            cartItems = query.list();
            for (Cart item : cartItems) {
                item.getMenu().getName(); //fetch before session close
                item.getMenu().getPrice();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.close();
        }
        return cartItems;
    }

    // Add a new cart item
    public void addCart(Cart cartItem) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(cartItem);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Update an existing cart item
    public void updateCart(Cart cartItem) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(cartItem);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    //remove an existing cart item
    public void deleteCartItem(Cart cartItem) {
        Transaction transaction = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession(); // Buka sesi Hibernate
            transaction = session.beginTransaction(); // Mulai transaksi
            session.delete(cartItem); // Hapus entitas UserAccounts
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

    // Clear all cart items for the given user
    public void clearUserCart(UserAccounts user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM model.Cart c WHERE c.userAccounts.userId = :userId");
            query.setParameter("userId", user.getUserId());
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback in case of an error
            }
        } finally {
            session.close();
        }
    }
}
