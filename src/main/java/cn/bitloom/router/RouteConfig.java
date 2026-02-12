package cn.bitloom.router;

import cn.bitloom.controller.IndexController;
import cn.bitloom.controller.HomePageController;
import cn.bitloom.controller.SettingsPageController;
import cn.bitloom.controller.FavoritesPageController;
import cn.bitloom.controller.TagsPageController;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 路由配置类
 * 定义页面路由映射关系
 *
 * @author bitloom
 */
@Getter
public class RouteConfig {

    /**
     * 路由路径常量
     */
    public static class Path {
        /**
         * The constant HOME.
         */
        public static final String HOME = "/";
        /**
         * The constant FAVORITES.
         */
        public static final String FAVORITES = "/favorites";
        /**
         * The constant TAGS.
         */
        public static final String TAGS = "/tags";
        /**
         * The constant SETTINGS.
         */
        public static final String SETTINGS = "/settings";
    }


    private final Map<String, Route> routes = new HashMap<>();

    /**
     * 初始化路由配置
     */
    public void init() {
        // 注册路由
        registerRoute(
            Path.HOME, 
            "主页", 
            HomePageController.class,
            (indexController) -> {
                if (indexController.getHomePageController() != null) {
                    indexController.getHomePageController().show();
                }
            },
            (indexController) -> {
                if (indexController.getHomePageController() != null) {
                    indexController.getHomePageController().hide();
                }
            }
        );
        
        registerRoute(
            Path.FAVORITES, 
            "收藏", 
            FavoritesPageController.class,
            (indexController) -> {
                if (indexController.getFavoritesPageController() != null) {
                    indexController.getFavoritesPageController().show();
                }
            },
            (indexController) -> {
                if (indexController.getFavoritesPageController() != null) {
                    indexController.getFavoritesPageController().hide();
                }
            }
        );
        
        registerRoute(
            Path.TAGS, 
            "标签管理", 
            TagsPageController.class,
            (indexController) -> {
                if (indexController.getTagsPageController() != null) {
                    indexController.getTagsPageController().show();
                }
            },
            (indexController) -> {
                if (indexController.getTagsPageController() != null) {
                    indexController.getTagsPageController().hide();
                }
            }
        );
        
        registerRoute(
            Path.SETTINGS, 
            "设置", 
            SettingsPageController.class,
            (indexController) -> {
                if (indexController.getSettingsPageController() != null) {
                    indexController.getSettingsPageController().show();
                }
            },
            (indexController) -> {
                if (indexController.getSettingsPageController() != null) {
                    indexController.getSettingsPageController().hide();
                }
            }
        );
    }

    /**
     * 注册路由
     *
     * @param path            路由路径
     * @param name            路由名称
     * @param controllerClass 控制器类
     * @param showAction      显示页面的动作
     * @param hideAction      隐藏页面的动作
     */
    private void registerRoute(String path, String name, Class<?> controllerClass, 
                              Consumer<IndexController> showAction, Consumer<IndexController> hideAction) {
        routes.put(path, new Route(path, name, controllerClass, showAction, hideAction));
    }

    /**
     * 根据路径获取路由
     *
     * @param path 路由路径
     * @return 路由对象
     */
    public Route getRoute(String path) {
        return routes.get(path);
    }

    /**
     * 路由类
     */
    public record Route(String path, String name, Class<?> controllerClass,
                       Consumer<IndexController> showAction, Consumer<IndexController> hideAction) {

    }
}
