package cn.bitloom;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.enums.FetchSourceEnums;
import cn.bitloom.store.Store;
import cn.bitloom.util.BrowserManager;
import cn.bitloom.util.ExecutorManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The type Stickers application.
 *
 * @author bitloom
 */
@Slf4j
public class StickersApplication extends Application {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.loadSettings();
        this.createAppDirsIfNotExist();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConstants.Stage.FXML));
        Scene scene = new Scene(loader.load(), AppConstants.Stage.WIDTH, AppConstants.Stage.HEIGHT);
        scene.setFill(Color.WHITE);
        stage.setTitle(AppConstants.Stage.TITLE);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(AppConstants.Stage.ICON))));
        stage.initStyle(StageStyle.UNIFIED);
        //监听窗口变化
        stage.setOnShowing(event -> {
            Store.stageHeight.set(stage.getHeight());
            Store.stageWidth.set(stage.getWidth());
        });
        stage.show();
    }

    @Override
    public void stop() {
        this.clearTempDirectory();
        BrowserManager.close();
        ExecutorManager.close();
    }

    private void createAppDirsIfNotExist() {
        try {
            if (!Files.exists(AppConstants.Base.APP_DIR)) {
                Files.createDirectories(AppConstants.Base.APP_DIR);
            }
            if (!Files.exists(AppConstants.Base.SETTINGS_FILE)) {
                Files.createFile(AppConstants.Base.SETTINGS_FILE);
            }
            if (!Files.exists(AppConstants.Base.LOGS_DIR)) {
                Files.createDirectories(AppConstants.Base.LOGS_DIR);
            }
            if (!Files.exists(AppConstants.Base.PLAYWRIGHT_DIR)) {
                Files.createDirectories(AppConstants.Base.PLAYWRIGHT_DIR);
            }
            if (!Files.exists(Store.savePath.get())) {
                Files.createDirectories(Store.savePath.get());
            }
            if (!Files.exists(AppConstants.Base.TEMP_DIR)) {
                Files.createDirectory(AppConstants.Base.TEMP_DIR);
            }
            if (!Files.exists(AppConstants.Base.FAVORITES_DIR)) {
                Files.createDirectory(AppConstants.Base.FAVORITES_DIR);
            }
        } catch (Exception e) {
            log.error("创建应用目录失败", e);
        }
    }

    private void loadSettings() {
        try {
            String json = Files.readString(AppConstants.Base.SETTINGS_FILE);
            if (json.trim().isEmpty()) {
                return;
            }
            JSONObject settings = JSON.parseObject(json);

            String savePath = settings.getString("savePath");
            if (Objects.nonNull(savePath)) {
                Store.savePath.set(Paths.get(savePath));
            }
            String browserPath = settings.getString("browserPath");
            if (Objects.nonNull(browserPath)) {
                Store.browserPath.set(Paths.get(browserPath));
            }
            JSONArray selectedFetchSources = settings.getJSONArray("selectedFetchSources");
            if (Objects.nonNull(selectedFetchSources) && !selectedFetchSources.isEmpty()) {
                Store.selectedFetchSources.clear();
                for (Object item : selectedFetchSources) {
                    try {
                        FetchSourceEnums dataSourceEnum = FetchSourceEnums.valueOf((String) item);
                        Store.selectedFetchSources.add(dataSourceEnum);
                    } catch (IllegalArgumentException e) {
                        log.warn("未知的数据源: {}", item);
                    }
                }
            }
        } catch (IOException e) {
            log.error("加载设置失败", e);
        }
    }

    private void clearTempDirectory() {
        try {
            Path tempDir = AppConstants.Base.TEMP_DIR;
            if (Files.exists(tempDir)) {
                try (Stream<Path> pathStream = Files.walk(tempDir)) {
                    pathStream
                            .sorted((a, b) -> -a.compareTo(b))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    log.error("删除文件失败: {}", path, e);
                                }
                            });
                    log.info("Temp目录已清空: {}", tempDir);
                }
            }
        } catch (Exception e) {
            log.error("清空Temp目录失败", e);
        }
    }
}