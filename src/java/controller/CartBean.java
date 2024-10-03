package controller;

import javax.inject.Named;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import dao.CartDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import model.Cart;
import model.Menu;
import model.UserAccounts;

@Named(value = "cartBean")
@SessionScoped
public class CartBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Cart> userCartItems;
    private BigDecimal totalPrice;

    @Inject
    private CartDAO cartDAO;

    @Inject
    private UserSessionBean userSession;

    /**
     * Creates a new instance of cartBean
     */
    public CartBean() {
    }

    @PostConstruct
    public void init() {
        loadCartItems(); // Load cart items when the bean is created
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Add an item to the cart
    public void addToCart(Menu menuItem) {
        if (userSession.isLoggedIn()) {
            UserAccounts currentUser = userSession.getCurrentUser();
            Cart existingCart = cartDAO.findCartByUserAndMenu(currentUser, menuItem);

            if (existingCart != null) {
                // If the item already exists in the cart, increase the quantity
                existingCart.setQuantity(existingCart.getQuantity() + 1);
                cartDAO.updateCart(existingCart);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Item quantity increased", null));
            } else {
                // Add new item to the cart with quantity 1
                Cart newCartItem = new Cart();
                newCartItem.setMenu(menuItem);
                newCartItem.setUserAccounts(currentUser);
                newCartItem.setQuantity(1);
                cartDAO.addCart(newCartItem);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Item added to cart", null));
            }
            loadCartItems();
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must be logged in to add items to the cart", null));
        }
    }

    // Method to get the cart items for the current user
    public void loadCartItems() {
        UserAccounts currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            userCartItems = cartDAO.getCartItemsByUser(currentUser);
            setTotalPrice(calculateTotalPrice());
        } else {
            userCartItems = new ArrayList<>(); // Empty cart if no user is logged in
        }
    }

    public List<Cart> getUserCartItems() {
        return userCartItems;
    }

    // Increase item quantity
    public void increaseQuantity(Cart cartItem) {
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartDAO.updateCart(cartItem);
        loadCartItems();
    }

    // Decrease item quantity
    public void decreaseQuantity(Cart cartItem) {
        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartDAO.updateCart(cartItem);
        } else {
            cartDAO.deleteCartItem(cartItem);
        }
        loadCartItems();
    }

    public BigDecimal calculateTotalPrice() {
        BigDecimal totalprice = BigDecimal.ZERO;
        for (Cart cart : userCartItems) {
            BigDecimal itemPrice = cart.getMenu().getPrice();
            BigDecimal itemTotalPrice = itemPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalprice = totalprice.add(itemTotalPrice);
        }
        return totalprice;
    }

    public String toCheckoutPage() {
        return "PaymentPage?faces-redirect=true"; // redirect to PaymentPage.xhtml
    }

    public void clearCart() {
        cartDAO.clearUserCart(userSession.getCurrentUser());
        loadCartItems();
    }
}
