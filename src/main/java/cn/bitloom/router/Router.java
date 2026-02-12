package cn.bitloom.router;

import cn.bitloom.holder.ButtonBarHolder;
import cn.bitloom.controller.IndexController;
import cn.bitloom.store.Store;
import lombok.extern.slf4j.Slf4j;

/**
 * 路由管理器
 * 处理页面导航和切换逻辑
 *
 * @author bitloom
 */
@Slf4j
public class Router {

    private final IndexController indexController;
    private final RouteConfig routeConfig;
    private String currentRoute;

    /**
     * 构造函数
     *
     * @param indexController 主控制器
     */
    public Router(IndexController indexController) {
        this.indexController = indexController;
        this.routeConfig = new RouteConfig();
        this.routeConfig.init();
        this.currentRoute = RouteConfig.Path.HOME;
    }

    /**
     * 导航到指定路径
     *
     * @param path 路由路径
     */
    public void navigate(String path) {
        // 检查路径是否存在
        RouteConfig.Route route = this.routeConfig.getRoute(path);
        if (route == null) {
            log.error("Route not found: {}", path);
            Store.statusText.set("路由未找到: " + path);
            return;
        }

        // 隐藏所有页面
        this.hideAllPages();

        // 显示目标页面
        this.showPage(path);

        // 更新 ButtonBar 按钮状态
        this.updateButtonBar(path);

        // 更新当前路由
        this.currentRoute = path;
    }

    /**
     * 更新指定路由的 ButtonBar 按钮状态（用于初始化）
     *
     * @param path 路由路径
     */
    public void updateButtonBarForRoute(String path) {
        this.updateButtonBar(path);
    }

    /**
     * 更新 ButtonBar 按钮状态
     *
     * @param path 当前路由路径
     */
    private void updateButtonBar(String path) {
        if (this.indexController.getButtonBarController() != null) {
            ButtonBarHolder config = null;

            if (RouteConfig.Path.HOME.equals(path)) {
                if (this.indexController.getHomePageController() != null) {
                    config = this.indexController.getHomePageController();
                }
            } else if (RouteConfig.Path.SETTINGS.equals(path)) {
                if (this.indexController.getSettingsPageController() != null) {
                    config = this.indexController.getSettingsPageController();
                }
            } else if (RouteConfig.Path.FAVORITES.equals(path)) {
                if (this.indexController.getFavoritesPageController() != null) {
                    config = this.indexController.getFavoritesPageController();
                }
            }

            this.indexController.getButtonBarController().updateButtons(config);
        }

        this.updateSidebar(path);
    }

    /**
     * 更新侧边栏状态
     *
     * @param path 当前路由路径
     */
    private void updateSidebar(String path) {
        if (this.indexController.getSideBarController() != null) {
            this.indexController.getSideBarController().updateActiveState(path);
        }
    }

    /**
     * 隐藏所有页面
     */
    private void hideAllPages() {
        // 遍历所有路由，执行隐藏动作
        for (RouteConfig.Route route : this.routeConfig.getRoutes().values()) {
            route.hideAction().accept(this.indexController);
        }
    }

    /**
     * 显示指定页面
     *
     * @param path 路由路径
     */
    private void showPage(String path) {
        // 获取目标路由
        RouteConfig.Route route = this.routeConfig.getRoute(path);
        if (route != null) {
            // 执行显示动作
            route.showAction().accept(this.indexController);
        }
    }

}
