package controller;

import dao.UserDAO;
import model.UserAccounts;
import javax.inject.Named;
import javax.inject.Inject;
import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import util.PasswordHasher;

@Named(value = "loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public LoginBean() {
    }

    private String username;
    private String password;
    private UserAccounts user;

    @Inject
    private UserDAO userDAO;
    @Inject
    private UserSessionBean userSession;
    @Inject
    private CartBean userCart;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserAccounts getUser() {
        return user;
    }

    public void setUser(UserAccounts user) {
        this.user = user;
    }

    public UserSessionBean getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSessionBean userSession) {
        this.userSession = userSession;
    }

    public String cancel() {
        return "HomePage?faces-redirect=true";
    }

    public String login() {
        // Hash password terlebih dahulu
        String hashedPassword = PasswordHasher.hashPassword(password);
        UserAccounts user = userDAO.getUserByNameAndPass(username, hashedPassword);

        if (user == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account not found or user credential was wrong.", null));
            return null; // Kembali ke halaman login jika user tidak ditemukan
        } else {
            // Menyimpan user ke dalam sesi setelah login berhasil
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedInUser", user);
            userSession.setLoggedIn(true);
            userSession.setCurrentUser(user);
            userCart.loadCartItems();
            // Check if the user is an admin
            boolean isAdmin = userDAO.isUserAdmin(user.getUserId());

            if (isAdmin) {
                // Redirect to Admin Dashboard
                return "AdminMenu.xhtml?faces-redirect=true";
            } else {
                // Redirect to regular HomePage
                return "HomePage.xhtml?faces-redirect=true";
            }
        }
    }

    public boolean authenticate(String username, String password) {
        UserDAO userDAO = new UserDAO();
        String hashedPassword = PasswordHasher.hashPassword(password);
        user = userDAO.getUserByNameAndPass(username, hashedPassword);
        return user != null;
    }
}
