package controller;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.util.List;
import model.Menu;
import dao.MenuDAO;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.ResponsiveOption;
import org.primefaces.model.file.UploadedFile;

@Named(value = "menuBean")
@ViewScoped
public class MenuBean implements Serializable {

    //Serializable
    private List<Menu> menuList;
    private List<Menu> mainMenus;
    private List<Menu> sideMenus;
    private List<Menu> dessertMenus;
    private List<Menu> drinksMenus;
    private List<ResponsiveOption> ro;
    private Menu selectedMenu;
    private byte[] menuimg;
    private UploadedFile uploadedFile;
    private String menuName;

    @Inject
    private MenuDAO dao;

    //create new instance of menuBean
    public MenuBean() {
    }

    @PostConstruct
    public void init() {
        mainMenus = dao.getMenuItemsByCategory("main");
        sideMenus = dao.getMenuItemsByCategory("side");
        dessertMenus = dao.getMenuItemsByCategory("dessert");
        drinksMenus = dao.getMenuItemsByCategory("drinks");
        menuList = dao.allMenuItems();
        selectedMenu = new Menu(); //initialized but null
        ro = new ArrayList<>();
        ro.add(new ResponsiveOption("1024", 3, 3));
        ro.add(new ResponsiveOption("768", 2, 2));
        ro.add(new ResponsiveOption("560", 1, 1));
    }

    public void refreshMenuList() {
        this.menuList = dao.allMenuItems(); // Fetch the latest menu list from the database
    }

    public Menu getMenu() {
        return selectedMenu;
    }

    public void setMenu(Menu menu) {
        this.selectedMenu = menu;
    }

    public List<Menu> getMenuList() {
        int count = menuList.size();
        System.out.println("!--Menu fetched: " + count + "--!");
        return menuList;
    }

    public List<Menu> getMainMenus() {
        return mainMenus;
    }

    public List<Menu> getSideMenus() {
        return sideMenus;
    }

    public List<Menu> getDessertMenus() {
        return dessertMenus;
    }

    public List<Menu> getDrinksMenus() {
        return drinksMenus;
    }

    public List<ResponsiveOption> getRo() {
        return ro;
    }

    public Menu getSelectedMenu() {
        return selectedMenu;
    }

    public void setSelectedMenu(Menu selectedmenu) {
        this.selectedMenu = selectedmenu;
    }

    public byte[] getMenuimg() {
        return menuimg;
    }

    public void setMenuimg(byte[] menuimg) {
        this.menuimg = menuimg;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void editMenu(Menu selectedmenu) {
        if (selectedmenu != null) {
            this.selectedMenu = selectedmenu;
            System.out.println("Selected menu for editing: " + selectedmenu.getName());
        } else {
            System.out.println("No menu selected for editing!");
        }
    }

    public void newMenu() {
        this.selectedMenu = new Menu();
        System.out.println("New menu is prepared!");
    }

    public void saveMenu() {
        if (selectedMenu.getImg() == null) {  // New menu, img should be empty
            if (uploadedFile != null) {
                // Convert the uploaded file to a byte array and set it as the image
                menuimg = uploadedFile.getContent();
                selectedMenu.setImg(menuimg);
                selectedMenu.setActive("ACTIVE");
                // Save the new menu to the database
                dao.addMenu(selectedMenu);
                // Display a success message
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "New menu has been added successfully."));
                refreshMenuList();
            } else {
                // Display an error message if no image was uploaded for the new menu
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Image is required for a new menu!"));
            }
        } else {
            // Editing an existing menu, image should already exist
            if (uploadedFile != null) {
                // If a new file is uploaded, update the image
                menuimg = uploadedFile.getContent();
                selectedMenu.setImg(menuimg);
            }
            // Save the updated menu to the database
            dao.saveMenu(selectedMenu);
            // Display a success message for editing
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Menu has been updated successfully."));
        }
    }

    public void deactivateMenu(Menu menu) {
        if (menu != null) {
            this.selectedMenu = menu;
            menu.setActive("INACTIVE");
            dao.saveMenu(menu);
        } else {
            System.out.println("No menu was chosen!");
        }
    }

    public void deleteMenu(Menu menu) {
        if (menu != null) {
            this.selectedMenu = menu;
            dao.deleteMenu(selectedMenu);
            refreshMenuList();
        } else {
            System.out.println("No menu was chosen!");
        }
    }
}
