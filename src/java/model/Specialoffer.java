package model;
// Generated Sep 29, 2024 6:26:55 AM by Hibernate Tools 4.3.1


import java.math.BigDecimal;

/**
 * Specialoffer generated by hbm2java
 */
public class Specialoffer  implements java.io.Serializable {


     private Integer specialOfferId;
     private Menu menu;
     private BigDecimal originalPrice;

    public Specialoffer() {
    }

    public Specialoffer(Menu menu, BigDecimal originalPrice) {
       this.menu = menu;
       this.originalPrice = originalPrice;
    }
   
    public Integer getSpecialOfferId() {
        return this.specialOfferId;
    }
    
    public void setSpecialOfferId(Integer specialOfferId) {
        this.specialOfferId = specialOfferId;
    }
    public Menu getMenu() {
        return this.menu;
    }
    
    public void setMenu(Menu menu) {
        this.menu = menu;
    }
    public BigDecimal getOriginalPrice() {
        return this.originalPrice;
    }
    
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }




}


