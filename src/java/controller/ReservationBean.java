package controller;

import dao.ReservationDAO;
import dao.OutletDAO;
import dao.UserDAO;
import model.Outlet;
import model.Reservation;
import model.UserAccounts;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

@ManagedBean(name = "reservationBean")
@ViewScoped
public class ReservationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reservation reservation;
    private List<Reservation> reservations;
    private List<Outlet> outletList;
    private Date today;
    private Date maxDate;
    private ReservationDAO reservationDAO;
    private OutletDAO outletDAO;
    @Inject
    private UserDAO userDAO;
    private Integer selectedOutletId;

    @PostConstruct
    public void init() {
        reservation = new Reservation();
        reservationDAO = new ReservationDAO();
        outletDAO = new OutletDAO();
        userDAO = new UserDAO();
        loadOutlets();
        today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, 7); // Misalnya, maksimum 30 hari ke depan
        maxDate = cal.getTime();
    }

    /**
     * Mengambil daftar outlet dari database menggunakan OutletDAO.
     */
    private void loadOutlets() {
        try {
            outletList = outletDAO.allOutlets();
            if (outletList.isEmpty()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Peringatan", "Tidak ada outlet yang tersedia.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Gagal memuat outlet.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            e.printStackTrace();
        }
    }

    /**
     * Menangani aksi submit pada formulir reservasi.
     *
     * @return Outcome navigasi
     */
    public String submitReservation() {
        try {
            // Mendapatkan user yang sedang login
            UserAccounts currentUser = getCurrentUser();
            if (currentUser == null) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Peringatan", "Silakan login sebelum melakukan reservasi.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null; // Tetap di halaman saat ini
            }

            // Validasi form
            if (isFormInvalid()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Peringatan", "Silakan isi semua bidang yang diperlukan.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            }

            // Validasi tanggal reservasi
            if (reservation.getReservedDate().before(today) || reservation.getReservedDate().after(maxDate)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tanggal reservasi harus antara hari ini dan maksimal 7 hari ke depan.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            }

            // Validasi durasi reservasi
            if (reservation.getDurationHours() <= 0) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Durasi reservasi harus lebih dari 0 jam.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            }

            // Mengambil Outlet berdasarkan selectedOutletId
            Outlet selectedOutlet = outletDAO.findById(selectedOutletId);
            if (selectedOutlet == null) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Outlet yang dipilih tidak ditemukan.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            }

            // Mengatur Outlet dan UserAccounts pada objek Reservation
            reservation.setOutlet(selectedOutlet);
            reservation.setUserAccounts(currentUser);
            reservation.setDateOnReservation(new Date());
            reservation.setStatus("Pending");

            // Simpan reservasi
            reservationDAO.saveReservation(reservation);

            // Tampilkan pesan sukses menggunakan Flash Scope
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sukses", "Reservasi berhasil dibuat."));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            // Reset form
            reservation = new Reservation();
            selectedOutletId = null;

            // Redirect ke HomePage.xhtml
            return "HomePage?faces-redirect=true";

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Gagal membuat reservasi. Silakan coba lagi.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Menangani aksi cancel pada formulir reservasi.
     *
     * @return Outcome navigasi
     */
    public String cancelReservation() {
        // Redirect ke HomePage.xhtml
        return "HomePage?faces-redirect=true";
    }

    /**
     * Memeriksa apakah pengguna sedang login.
     *
     * @return True jika pengguna login, false jika tidak.
     */
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Mendapatkan objek UserAccounts dari sesi.
     *
     * @return Objek UserAccounts yang sedang login atau null jika tidak ada.
     */
    private UserAccounts getCurrentUser() {
        return (UserAccounts) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInUser");
    }

    /**
     * Memeriksa apakah formulir reservasi memiliki field yang tidak valid.
     *
     * @return True jika ada field yang tidak valid, false jika semua field
     * valid.
     */
    private boolean isFormInvalid() {
        return reservation.getCustomerName() == null || reservation.getCustomerName().trim().isEmpty()
                || reservation.getPhoneNumber() == null || reservation.getPhoneNumber().trim().isEmpty()
                || selectedOutletId == null
                || reservation.getReservedDate() == null
                || reservation.getReservedTime() == null
                || reservation.getDurationHours() <= 0;
    }

    // Getter dan Setter
    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public List<Outlet> getOutletList() {
        return outletList;
    }

    public void setOutletList(List<Outlet> outletList) {
        this.outletList = outletList;
    }

    public Date getToday() {
        return today;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public Integer getSelectedOutletId() {
        return selectedOutletId;
    }

    public void setSelectedOutletId(Integer selectedOutletId) {
        this.selectedOutletId = selectedOutletId;
    }
    
    public List<Reservation> getReservations() {
        this.reservations = reservationDAO.getAllReservations();
        return reservations;
    }

}
