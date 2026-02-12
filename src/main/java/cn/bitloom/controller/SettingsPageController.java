package cn.bitloom.controller;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.enums.FetchSourceEnums;
import cn.bitloom.holder.ButtonBarHolder;
import cn.bitloom.holder.PageHolder;
import cn.bitloom.store.Store;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The type Settings page controller.
 *
 * @author bitloom
 */
@Slf4j
public class SettingsPageController implements Initializable, ButtonBarHolder, PageHolder {

    @FXML
    private VBox settingsPage;
    @FXML
    private TextField savePathTextField;
    @FXML
    private Button browseButton;
    @FXML
    private TextField browserPathTextField;
    @FXML
    private Button browseBrowserButton;
    @FXML
    private VBox fetchSourceContainer;

    private final Map<FetchSourceEnums, SimpleBooleanProperty> fetchSourceProperties = new TreeMap<>();

    @Getter
    @Setter
    private IndexController indexController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.browseButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择保存目录");
            Path path = Paths.get(this.savePathTextField.getText());
            directoryChooser.setInitialDirectory(path.toFile());
            File selectedDirectory = directoryChooser.showDialog(this.settingsPage.getScene().getWindow());
            if (Objects.nonNull(selectedDirectory)) {
                this.savePathTextField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        this.browseBrowserButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择浏览器可执行文件");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("可执行文件", "*.exe"));
            Path path = Paths.get(this.browserPathTextField.getText());
            if (Files.exists(path)) {
                fileChooser.setInitialDirectory(path.getParent().toFile());
            }
            File selectedFile = fileChooser.showOpenDialog(this.settingsPage.getScene().getWindow());
            if (Objects.nonNull(selectedFile)) {
                this.browserPathTextField.setText(selectedFile.getAbsolutePath());
            }
        });

        this.initFetchSources();
        this.loadFromStore();
    }

    private void initFetchSources() {
        for (FetchSourceEnums fetchSourceEnums : FetchSourceEnums.values()) {
            VBox row = new VBox();
            row.getStyleClass().add("settings-row");

            HBox header = new HBox(16);
            header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            HBox.setHgrow(header, javafx.scene.layout.Priority.ALWAYS);

            Label titleLabel = new Label(fetchSourceEnums.getName());
            titleLabel.getStyleClass().add("settings-row-title");

            CheckBox checkBox = new CheckBox();
            SimpleBooleanProperty checkProperty = new SimpleBooleanProperty(false);
            checkProperty.bindBidirectional(checkBox.selectedProperty());
            checkBox.getStyleClass().add("settings-toggle");

            header.getChildren().addAll(titleLabel, checkBox);

            Label descriptionLabel = new Label(fetchSourceEnums.getDesc());
            descriptionLabel.getStyleClass().add("settings-row-subtitle");
            descriptionLabel.setWrapText(true);

            row.getChildren().addAll(header, descriptionLabel);

            this.fetchSourceContainer.getChildren().add(row);
            this.fetchSourceProperties.put(fetchSourceEnums, checkProperty);
        }
        VBox divider = new VBox();
        divider.getStyleClass().add("settings-divider");
        this.fetchSourceContainer.getChildren().add(divider);
    }

    private void loadFromStore() {
        this.savePathTextField.setText(Store.savePath.get().toString());
        this.browserPathTextField.setText(Store.browserPath.get().toString());
        for (Map.Entry<FetchSourceEnums, SimpleBooleanProperty> entry : this.fetchSourceProperties.entrySet()) {
            entry.getValue().set(Store.selectedFetchSources.contains(entry.getKey()));
        }
    }

    /**
     * Show settings page.
     */
    @Override
    public void show() {
        this.settingsPage.setVisible(true);
        this.settingsPage.setManaged(true);
        this.loadFromStore();
    }

    /**
     * Hide settings page.
     */
    @Override
    public void hide() {
        this.settingsPage.setVisible(false);
        this.settingsPage.setManaged(false);
    }

    /**
     * Save settings.
     */
    public void save() {
        Store.savePath.set(Paths.get(savePathTextField.getText()));
        Store.browserPath.set(Paths.get(browserPathTextField.getText()));
        Store.selectedFetchSources.clear();
        this.fetchSourceProperties.forEach((key, value) -> {
            if (value.get()) {
                Store.selectedFetchSources.add(key);
            }
        });

        this.saveToFile();
    }

    private void saveToFile() {
        try {
            JSONObject settings = new JSONObject();
            settings.put("savePath", Store.savePath.get().toString());
            settings.put("browserPath", Store.browserPath.get().toString());
            settings.put("selectedFetchSources", Store.selectedFetchSources.stream().map(FetchSourceEnums::name).toList());
            Files.writeString(AppConstants.Base.SETTINGS_FILE, JSON.toJSONString(settings));
            Store.statusText.set("保存成功");
        } catch (IOException e) {
            log.error("保存失败", e);
            Store.statusText.set("保存失败");
        }
    }

    /**
     * Reset settings.
     */
    public void reset() {
        Store.savePath.set(AppConstants.Base.APP_DIR.resolve("outputs"));
        Store.browserPath.set(Paths.get("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"));
        Store.selectedFetchSources.clear();
        Store.selectedFetchSources.add(FetchSourceEnums.DOUYIN);
        this.loadFromStore();
        Store.statusText.set("重置成功");
    }

    @Override
    public List<ButtonBarHolder.ButtonConfig> getButtonConfigs() {
        return List.of(
                new ButtonBarHolder.ButtonConfig(
                        "saveSettingsButton",
                        "Save",
                        "dynamic-btn",
                        event -> this.save()
                ),
                new ButtonBarHolder.ButtonConfig(
                        "resetSettingsButton",
                        "Reset",
                        "dynamic-btn",
                        event -> this.reset()
                )
        );
    }

}
