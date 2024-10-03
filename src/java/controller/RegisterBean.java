package controller;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import model.UserAccounts;
import util.PasswordHasher;
import dao.UserDAO;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

@Named(value = "registerBean")
@ViewScoped
public class RegisterBean implements Serializable {

    /**
     * Creates a new instance of RegisterBean
     */
    public RegisterBean() {
    }
    //Entity for hibernate and Bean attributes for EL
    private UserAccounts user;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String passwordConfirmation;
    @Inject
    private UserDAO dao;

    //Getter Setters
    public UserAccounts getUser() {
        return user;
    }

    public void setUser(UserAccounts user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    // Email Validator
    public void validateEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = value.toString();
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";  // Simple email pattern

        if (!email.matches(emailPattern)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email format.", null);
            throw new ValidatorException(message);
        }
    }

    // Phone Number Validator
    public void validatePhoneNumber(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String phoneNumber = value.toString();
        String phonePattern = "^[0-9]{10,15}$";  // Allows only digits between 10 and 15 digits

        if (!phoneNumber.matches(phonePattern)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid phone number. Only digits allowed (10-15 digits).", null);
            throw new ValidatorException(message);
        }
    }
    
    //Functionality Methods
    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!password.equals(passwordConfirmation)) {
            context.addMessage("registerForm:passwordConfirmation",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match.", null));
            return null;
        }
        // Check if the username is already taken
        if (!dao.isUniqueAccount(username)) {
            context.addMessage("registerForm:username",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "A user with that username already exists.", null));
            return null;
        }

        //create the mapped object instance for new user
        UserAccounts newUser = new UserAccounts();
        newUser.setUsername(username);
        newUser.setPassword(PasswordHasher.hashPassword(password));
        newUser.setEmail(email);
        newUser.setUserPnum(phoneNumber);

        try {
            dao.addUser(newUser);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful!", null));
            return "LoginPage";
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed: " + ex.getMessage(), null));
            return null;
        }
    }

    //cancel method
    public String cancel() {
        return "HomePage";
    }
}
