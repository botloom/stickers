package cn.bitloom.controller;

import cn.bitloom.router.Router;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Primary controller.
 *
 * @author bitloom
 */
@Slf4j
public class IndexController implements Initializable {

    @FXML
    @Getter
    private HBox rootContainer;
    @FXML
    @Getter
    private ButtonBarController buttonBarController;
    @FXML
    @Getter
    private SideBarController sideBarController;
    @FXML
    @Getter
    private HomePageController homePageController;
    @FXML
    @Getter
    private FavoritesPageController favoritesPageController;
    @FXML
    @Getter
    private TagsPageController tagsPageController;
    @FXML
    @Getter
    private SettingsPageController settingsPageController;

    @Getter
    private Router router;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.router = new Router(this);

        this.buttonBarController.setIndexController(this);
        this.sideBarController.setIndexController(this);
        this.homePageController.setIndexController(this);
        this.favoritesPageController.setIndexController(this);
        this.tagsPageController.setIndexController(this);
        this.settingsPageController.setIndexController(this);

        this.initializeButtonBar();
    }

    /**
     * 初始化 ButtonBar（设置默认页面的按钮）
     */
    private void initializeButtonBar() {
        if (this.router != null && this.buttonBarController != null) {
            this.router.updateButtonBarForRoute(cn.bitloom.router.RouteConfig.Path.HOME);
        }
    }

    /**
     * 导航到指定路径
     *
     * @param path 路由路径
     */
    public void navigate(String path) {
        if (router != null) {
            router.navigate(path);
        }
    }

    /**
     * 切换侧边栏显示状态
     */
    public void toggleSidebar() {
        if (sideBarController != null) {
            if (sideBarController.isSidebarVisible()) {
                sideBarController.hide();
            } else {
                sideBarController.show();
            }
        }
    }

}