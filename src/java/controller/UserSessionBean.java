package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import model.UserAccounts;
import dao.UserDAO;
import java.io.IOException;
import util.PasswordHasher;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "userSessionBean")
@SessionScoped
public class UserSessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean loggedIn = false;
    private UserAccounts currentUser;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    @Inject
    private UserDAO userDAO;
    @Inject
    private CartBean usercart;

    // Getter dan Setter untuk loggedIn
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    // Getter dan Setter untuk currentUser
    public UserAccounts getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccounts currentUser) {
        this.currentUser = currentUser;
    }

    // Metode untuk memuat profil pengguna (ditambahkan)
    public void loadUserProfile() {
        if (currentUser == null) {
            // Debugging log
            System.out.println("User profile is null. Redirecting to login.");
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("LoginPage.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Debugging log untuk memastikan profil user sudah terisi
            System.out.println("User profile loaded: " + currentUser.getUsername());
        }
    }

    // Getter dan Setter untuk password fields
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Method untuk mendapatkan username
    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }

    // Method untuk mendapatkan email
    public String getEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }

    // Method untuk mendapatkan nomor telepon pengguna
    public String getUserPnum() {
        return currentUser != null ? currentUser.getUserPnum() : "";
    }
    // Getter untuk userId

    public Integer getUserId() {
        if (currentUser != null) {
            return currentUser.getUserId();
        }
        return null; // Kembalikan null jika tidak ada pengguna yang login
    }

// Navigasi berdasarkan status login
    public String navigateToOrderOrLogin() {
        return loggedIn ? "OrderPage.xhtml?faces-redirect=true" : "LoginPage.xhtml?faces-redirect=true";
    }
// Method to delete user profile

    public String deleteProfile() {
        if (currentUser != null) {
            try {
                userDAO.deleteUser(currentUser);
                loggedIn = false; // Logout setelah penghapusan
                currentUser = null;
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                return "LoginPage.xhtml?faces-redirect=true";  // Redirect ke halaman login
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete profile."));
                return null;
            }
        } else {
            return "LoginPage.xhtml?faces-redirect=true"; // Redirect ke halaman login jika tidak ada user
        }
    }

    // Method to change user password
    public String changePassword() {
        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "All fields are required.", null));
            return null;
        }

        if (!PasswordHasher.hashPassword(currentPassword).equals(currentUser.getPassword())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current password is incorrect.", null));
            return null;
        }

        if (!newPassword.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "New password and confirm password do not match.", null));
            return null;
        }

        try {
            currentUser.setPassword(PasswordHasher.hashPassword(newPassword));
            userDAO.updateUser(currentUser); // Menggunakan injeksi UserDAO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password changed successfully.", null));
            return "ProfilePage.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occurred while changing password.", null));
            return null;
        }
    }

    // Method untuk update profil
    public String updateProfile() {
        try {
            if (currentUser != null) {
                userDAO.updateUser(currentUser);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile updated successfully.", null));
                return "ProfilePage.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to update profile. User not found.", null));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occurred while updating profile.", null));
            return null;
        }
    }

    // Method untuk logout
    public void logout() {
        this.loggedIn = false;
        currentUser = null;
        usercart.clearCart();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        try {
            // Redirect to the home page or home page after logout
            FacesContext.getCurrentInstance().getExternalContext().redirect("HomePage.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
