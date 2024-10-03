package dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import model.Reservation;
import model.Outlet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class ReservationDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entityManager;

    // Menggunakan SessionFactory dari HibernateUtil
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    /**
     * Menyimpan objek Reservation ke database.
     *
     * @param reservation Objek Reservation yang akan disimpan.
     */
    public void saveReservation(Reservation reservation) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();

            // Set status to 'reserved'
            reservation.setStatus("reserved");

            // Cek dan set outlet jika tidak null
            if (reservation.getOutlet() != null) {
                // Mengambil entitas Outlet yang dikelola dari database menggunakan outletId
                Outlet managedOutlet = (Outlet) session.get(Outlet.class, reservation.getOutlet().getOutletId());
                if (managedOutlet == null) {
                    throw new RuntimeException("Outlet dengan ID " + reservation.getOutlet().getOutletId() + " tidak ditemukan.");
                }
                reservation.setOutlet(managedOutlet); // Set entitas yang dikelola
            }

            // Set userAccounts jika belum diset
            if (reservation.getUserAccounts() == null) {
                throw new RuntimeException("UserAccounts tidak diset pada objek Reservation.");
            }

            // Menyimpan objek Reservation ke database
            session.save(reservation);

            // Commit transaksi
            transaction.commit();
            System.out.println("Reservasi berhasil disimpan ke database dengan ID: " + reservation.getReserveId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saat menyimpan reservasi: " + e.getMessage());
            e.printStackTrace();
            throw e; // Lempar ulang exception agar dapat ditangani di lapisan atas
        } finally {
            session.close(); // Pastikan sesi ditutup
        }
    }

    /**
     * Mengambil daftar reservasi berdasarkan userId.
     *
     * @param userId ID pengguna yang ingin diambil reservasinya.
     * @return Daftar objek Reservation milik pengguna tertentu.
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        // Menggunakan TypedQuery untuk mengambil reservasi untuk pengguna tertentu
        TypedQuery<Reservation> query = entityManager.createQuery(
                "FROM Reservation r WHERE r.userAccounts.userId = :userId", Reservation.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<Reservation> getAllReservations() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Reservation> res = null; //clear list of previous objects.
        try {
            session.beginTransaction();
            res = session.createCriteria(Reservation.class).list();
            int count = res.size();
            for (Reservation r : res) {
                r.getOutlet().getOutletName();
                r.getOutlet().getAddress();
                r.getUserAccounts().getUserId();
                r.getUserAccounts().getUsername();
            }
            System.out.println("!--menu list generated: " + count + " --!"); //debugging purpose
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return res;
    }

    public void updateReservation(Reservation res) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(res);
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

    public void deleteRes(Reservation res) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession(); // Buka sesi Hibernate
            transaction = session.beginTransaction(); // Mulai transaksi
            session.delete(res); // Hapus entitas UserAccounts
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
