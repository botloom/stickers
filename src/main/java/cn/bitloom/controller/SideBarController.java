package cn.bitloom.controller;

import cn.bitloom.holder.PageHolder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Sidebar controller.
 *
 * @author bitloom
 */
public class SideBarController implements Initializable, PageHolder {

    @FXML
    private VBox sideBar;
    @FXML
    private HBox homeOption;
    @FXML
    private HBox favoritesOption;
    @FXML
    private HBox tagsOption;
    @FXML
    private HBox settingsOption;

    @Getter
    @Setter
    private IndexController indexController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.hide();
        this.homeOption.setOnMouseClicked(event -> {
            if (this.indexController != null) {
                this.indexController.navigate(cn.bitloom.router.RouteConfig.Path.HOME);
            }
        });
        this.favoritesOption.setOnMouseClicked(event -> {
            if (this.indexController != null) {
                this.indexController.navigate(cn.bitloom.router.RouteConfig.Path.FAVORITES);
            }
        });
        this.tagsOption.setOnMouseClicked(event -> {
            if (this.indexController != null) {
                this.indexController.navigate(cn.bitloom.router.RouteConfig.Path.TAGS);
            }
        });
        this.settingsOption.setOnMouseClicked(event -> {
            if (this.indexController != null) {
                this.indexController.navigate(cn.bitloom.router.RouteConfig.Path.SETTINGS);
            }
        });

        this.homeOption.getStyleClass().add("active");
    }

    /**
     * 更新激活状态
     *
     * @param path 当前路由路径
     */
    public void updateActiveState(String path) {
        this.homeOption.getStyleClass().remove("active");
        this.favoritesOption.getStyleClass().remove("active");
        this.tagsOption.getStyleClass().remove("active");
        this.settingsOption.getStyleClass().remove("active");

        if (cn.bitloom.router.RouteConfig.Path.HOME.equals(path)) {
            this.homeOption.getStyleClass().add("active");
        } else if (cn.bitloom.router.RouteConfig.Path.FAVORITES.equals(path)) {
            this.favoritesOption.getStyleClass().add("active");
        }

        else if (cn.bitloom.router.RouteConfig.Path.TAGS.equals(path)) {
            this.tagsOption.getStyleClass().add("active");
        } else if (cn.bitloom.router.RouteConfig.Path.SETTINGS.equals(path)) {
            this.settingsOption.getStyleClass().add("active");
        }
    }

    /**
     * Show sidebar.
     */
    @Override
    public void show() {
        this.sideBar.setVisible(true);
        this.sideBar.setManaged(true);
    }

    /**
     * Hide sidebar.
     */
    @Override
    public void hide() {
        this.sideBar.setVisible(false);
        this.sideBar.setManaged(false);
    }

    /**
     * Is sidebar visible boolean.
     *
     * @return the boolean
     */
    public boolean isSidebarVisible() {
        return this.sideBar != null && this.sideBar.isVisible();
    }

}
