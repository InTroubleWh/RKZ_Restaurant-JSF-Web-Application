package controller;

import dao.UserDAO;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.annotation.PostConstruct;
import model.UserAccounts;

@Named(value = "contactUsBean")
@RequestScoped
public class ContactUsBean {

    private String name;
    private String email;
    private String phone;
    private String subject;
    private String question;

    @Inject
    private UserSessionBean userSessionBean;

    @Inject
    private UserDAO userDAO;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    // Initialization method to pre-fill form fields if user is logged in
    @PostConstruct
    public void init() {
        if (userSessionBean.isLoggedIn()) {
            this.name = userSessionBean.getUsername();
            this.email = userSessionBean.getEmail();
            this.phone = userSessionBean.getUserPnum();
        }
    }

    // Method to handle form submission
    public String submitQuestion() {
        System.out.println("User session: " + userSessionBean.getCurrentUser());
        System.out.println("Subject: " + subject + ", Question: " + question);

        // Validasi input
        if (subject == null || subject.trim().isEmpty() || question == null || question.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Subject and question cannot be empty."));
            return null; // Tetap di halaman yang sama jika ada error
        }
        try {
            UserAccounts currentUser = userSessionBean.getCurrentUser(); // Ambil user yang login
            System.out.println("Current user: " + currentUser);

            if (currentUser != null) {
                userDAO.saveQuestion(currentUser, subject, question);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Success", "Your question has been submitted successfully!"));
                return "HomePage.xhtml?faces-redirect=true";  // Redirect ke halaman konfirmasi
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error", "User must be logged in to submit a question."));
                return null;
            }
        } catch (Exception e) {
            // Log kesalahan
            e.printStackTrace();
            // Berikan umpan balik ke pengguna
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Error submitting your question. Please try again."));
            return null; // Tetap di halaman yang sama jika terjadi error
        }
    }
}
