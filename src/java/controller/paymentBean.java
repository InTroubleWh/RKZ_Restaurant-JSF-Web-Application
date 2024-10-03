package controller;

import javax.inject.Named;
import model.Transactions;
import model.Outlet;
import model.Transactionitems;
import model.Transactions;
import java.util.List;
import dao.OutletDAO;
import dao.TransactionsDAO;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import model.Cart;

@Named(value = "paymentBean")
@ViewScoped
public class paymentBean implements Serializable {

    private List<Outlet> outlets;
    private Outlet selectedOutlet;
    private int selectOutletId;
    private String address;
    private String selectedPaymentMethod;
    @Inject
    private CartBean cartBean;
    @Inject
    private UserSessionBean userSession;

    /**
     * Creates a new instance of paymentBean
     */
    public paymentBean() {
    }

    @PostConstruct
    public void init() {
        outlets = new OutletDAO().allOutlets();
        
    }

    public List<Outlet> getOutlets() {
        return outlets;
    }

    public void setOutlets(List<Outlet> outlets) {
        this.outlets = outlets;
    }

    public Outlet getSelectOutlet() {
        return selectedOutlet;
    }

    public void setSelectOutlet(Outlet selectOutlet) {
        this.selectedOutlet = selectOutlet;
    }

    public int getSelectOutletId() {
        return selectOutletId;
    }

    public void setSelectOutletId(int selectOutletId) {
        this.selectOutletId = selectOutletId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(String selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public Outlet getSelectedOutlet() {
        return selectedOutlet;
    }

    public void setSelectedOutlet(Outlet selectedOutlet) {
        this.selectedOutlet = selectedOutlet;
    }

    public String processPayment() {
        if (userSession == null || cartBean == null) {
            System.out.println("One of the beans is not injected properly.");
            return null;
        }
        OutletDAO dao = new OutletDAO();
        selectedOutlet = dao.findById(selectOutletId);
        //create new Transactions object
        Transactions t = new Transactions();
        TransactionsDAO tdao = new TransactionsDAO();
        t.setUserAccounts(this.userSession.getCurrentUser());
        t.setOutlet(this.selectedOutlet);
        t.setAddress(this.address);
        t.setPaymentMethod(this.selectedPaymentMethod);
        t.setTotalAmount(cartBean.getTotalPrice());
        t.setTransactionDate(new Date());

        System.out.println("Process Payment method Called. ");
        //save with DAO
        tdao.saveTransactions(t);
        //processing transactionitem
        for(Cart cart : cartBean.getUserCartItems()) {
            Transactionitems ti = new Transactionitems();
            ti.setItemName(cart.getMenu().getName());
            ti.setTransactions(t);
            ti.setMenu(cart.getMenu());
            ti.setPrice(cart.getMenu().getPrice());
            ti.setQuantity(cart.getQuantity());
            tdao.saveTransactionItem(ti);
        }
        cartBean.clearCart();
        return "MenuPage.xhtml";
    }
}
