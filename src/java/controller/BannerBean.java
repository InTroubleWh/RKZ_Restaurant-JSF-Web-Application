package controller;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import model.Banner;
import java.util.List;
import dao.BannerDAO;

@Named(value = "bannerBean")
@ViewScoped
public class BannerBean implements Serializable{

    /**
     * Creates a new instance of BannerBean
     */
    public BannerBean() {
    }
    
    public List<Banner> allBanners() {
        BannerDAO dao = new BannerDAO();
        List<Banner> banners = dao.getBanners();
        return banners;
    }
}
