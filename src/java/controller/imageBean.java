package controller;

import dao.MenuDAO;
import dao.BannerDAO;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import model.Menu;
import model.Banner;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author kusma
 */
@Named(value = "imageBean")
@RequestScoped
public class imageBean {

    private StreamedContent menuImage;
    private Menu menu;
    private StreamedContent bannerImage;

    /**
     * Creates a new instance of imageBean
     */
    public imageBean() {
    }

    public StreamedContent getMenuImage() {
        MenuDAO dao = new MenuDAO();
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return DefaultStreamedContent.builder()
                .stream(() -> null) // Returning null because we don't have a real stream yet
                .contentType("image/png") // Set to a default MIME type
                .build();
        } else {
            String contextRetrievedId = context.getExternalContext().getRequestParameterMap().get("imageId");
            int imageId = Integer.valueOf(contextRetrievedId);
            System.out.println("Fecthing image with ID: " + imageId);
            Menu menu = dao.getMenuById(imageId);
            return DefaultStreamedContent.builder()
                .stream(() -> new ByteArrayInputStream(menu.getImg()))
                .contentType("image/png") // Change this if necessary
                .name(menu.getName()+".png") // Optional: customize the file name
                .build();
        }
    }
    
    public StreamedContent getBannerImage() {
        BannerDAO dao = new BannerDAO();
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return DefaultStreamedContent.builder()
                .stream(() -> null) // Returning null because we don't have a real stream yet
                .contentType("image/png") // Set to a default MIME type
                .build();
        } else {
            String contextRetrievedId = context.getExternalContext().getRequestParameterMap().get("bannerId");
            int bannerId = Integer.valueOf(contextRetrievedId);
            System.out.println("Fecthing image with ID: " + bannerId);
            Banner banner = dao.getBannerById(bannerId);
            return DefaultStreamedContent.builder()
                .stream(() -> new ByteArrayInputStream(banner.getImg()))
                .contentType("image/png") // Change this if necessary
                .name(banner.getName()+".png") // Optional: customize the file name
                .build();
        }
    }
}
