package dao;

import util.HibernateUtil;
import model.Transactions;
import java.util.List;
import model.Transactionitems;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

public class TransactionsDAO {

    public List<Transactions> allTransactions() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Transactions> tses = null; //clear list of previous objects.
        try {
            session.beginTransaction();
            tses = session.createCriteria(Transactions.class).list();
            int count = tses.size();
            System.out.println("!--menu list generated: " + count + " --!"); //debugging purpose
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return tses;
    }
    
    public void saveTransactions(Transactions t) {
        Session session = null;
        Transaction transaction = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(t);
            transaction.commit();
        } catch(Exception ex) {
            ex.printStackTrace();
            transaction.rollback();
        } finally {
            session.close();
        }
    }
    
    public void saveTransactionItem(Transactionitems ti) {
        Session session = null;
        Transaction transaction = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(ti);
            transaction.commit();
        } catch(Exception ex) {
            ex.printStackTrace();
            transaction.rollback();
        } finally {
            session.close();
        }
    }
}
