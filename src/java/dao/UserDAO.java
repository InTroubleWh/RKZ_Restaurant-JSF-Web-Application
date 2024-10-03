package dao;

import model.UserAccounts;
import model.ContactQuestions;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named(value = "userDAO")
@ApplicationScoped
public class UserDAO implements Serializable {
    private static final long serialVersionUID = 1L;

    private UserAccounts user;

    public UserAccounts getUserByNameAndPass(String uname, String pass) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM UserAccounts WHERE username = :username AND password = :password";
            Query hq = session.createQuery(hql);
            hq.setParameter("username", uname);
            hq.setParameter("password", pass);  // Password sudah dalam bentuk hash
            user = (UserAccounts) hq.uniqueResult();
        } catch (Exception ex) {
            System.out.println("Exception while retrieving user account = " + ex);
        } finally {
            session.close();
        }
        return user;
    }

    public void updateUser(UserAccounts user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(user);
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

    public void addUser(UserAccounts user) {
        Session session = null;
        Transaction transaction = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        }
        catch (Exception ex) {
            System.out.println("Exception while registering user account = "+ex);
            if(transaction != null) {
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }
    }

    // Metode untuk menghapus pengguna dari tabel UserAccounts
    public void deleteUser(UserAccounts user) {
        Transaction transaction = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession(); // Buka sesi Hibernate
            transaction = session.beginTransaction(); // Mulai transaksi
            session.delete(user); // Hapus entitas UserAccounts
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

    public boolean isUniqueAccount(String username) {
        boolean isUnique = false;
        Session session = HibernateUtil.getSessionFactory().openSession();;
        try { 
            String hql = "SElECT COUNT(*) FROM UserAccounts WHERE username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);
            Long count = (Long) query.uniqueResult();
            isUnique = (count == 0);
        } catch (Exception ex) {
            System.out.println("Exception while checking user's uniqueness"+ex);
        } finally {
            session.close();
        }
        return isUnique;
    }

    // Tambahan: Metode untuk menyimpan pertanyaan ke tabel contact_questions
    public void saveQuestion(UserAccounts userAccounts, String subject, String question) {
        Transaction transaction = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession(); // Buka sesi Hibernate
            transaction = session.beginTransaction(); // Mulai transaksi

            // Membuat entitas ContactQuestions dan mengisinya
            ContactQuestions contactQuestion = new ContactQuestions();
            contactQuestion.setUserAccounts(userAccounts); // Set UserAccounts
            contactQuestion.setSubject(subject);
            contactQuestion.setQuestion(question);
            contactQuestion.setCreatedAt(new Date()); // Set createdAt saat ini

            // Simpan ke database
            session.save(contactQuestion);
            transaction.commit(); // Komit transaksi
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback jika terjadi error
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Tutup sesi Hibernate
            }
        }
    }
    
    public boolean isUserAdmin(int userId) {
    String hql = "SELECT COUNT(a) FROM Admin a WHERE a.userAccounts.userId = :userId";
    Long count = (Long) HibernateUtil.getSessionFactory().openSession().createQuery(hql)
        .setParameter("userId", userId)
        .uniqueResult();
    return count > 0;
}
}
