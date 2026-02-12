package cn.bitloom.constant;

import java.nio.file.Path;
import java.nio.file.Paths;

import cn.bitloom.store.Store;

/**
 * The type App constants.
 *
 * @author bitloom
 */
public class AppConstants {

    private AppConstants() {
    }

    public static class Base {
        private Base() {
        }

        public static final String USER_HOME = System.getProperty("user.home");
        public static final Path APP_DIR = Paths.get(USER_HOME, ".stickers");
        public static final Path SETTINGS_FILE = APP_DIR.resolve("settings.json");
        public static final Path LOGS_DIR = APP_DIR.resolve("logs");
        public static final Path PLAYWRIGHT_DIR = APP_DIR.resolve("playwright_data");
        public static final Path TEMP_DIR = Store.savePath.get().resolve("temp");
        public static final Path FAVORITES_DIR = Store.savePath.get().resolve("favorites");
    }

    /**
     * The type Window.
     */
    public static class Stage {
        private Stage() {
        }

        /**
         * The constant WIDTH.
         */
        public static final double WIDTH = 800;
        /**
         * The constant HEIGHT.
         */
        public static final double HEIGHT = 500;
        /**
         * The constant TITLE.
         */
        public static final String TITLE = "Sticker";
        /**
         * The constant FXML.
         */
        public static final String FXML = "/cn/bitloom/index.fxml";
        /**
         * The constant ICON.
         */
        public static final String ICON = "/cn/bitloom/images/icon.png";
    }

    /**
     * The type Card.
     */
    public static class Card {
        private Card() {
        }

        /**
         * The constant GRID_COLUMNS.
         */
        public static final int GRID_COLUMNS = 5;
        /**
         * The constant GRID_GAP.
         */
        public static final int GRID_GAP = 16;
    }
}
