package cn.bitloom.controller;

import cn.bitloom.holder.ButtonBarHolder;
import cn.bitloom.holder.PageHolder;
import cn.bitloom.model.ImageData;
import cn.bitloom.store.Store;
import cn.bitloom.util.ExecutorManager;
import cn.bitloom.vm.FavoritesPageViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The type Favorites page controller.
 *
 * @author bitloom
 */
@Slf4j
public class FavoritesPageController implements Initializable, PageHolder, ButtonBarHolder {

    @FXML
    private VBox favoritesPage;
    @FXML
    private FlowPane favoritesGrid;

    @Getter
    @Setter
    private IndexController indexController;

    @Getter
    private FavoritesPageViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.viewModel = new FavoritesPageViewModel();
        this.favoritesGrid.visibleProperty().bind(Bindings.isEmpty(this.viewModel.getImageDataList()).not());
        this.favoritesGrid.managedProperty().bind(Bindings.isEmpty(this.viewModel.getImageDataList()).not());
        this.viewModel.getImageDataList().addListener((ListChangeListener<ImageData>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    this.renderFavorites(change.getAddedSubList(), true);
                }
                if (change.wasRemoved()) {
                    this.renderFavorites(change.getRemoved(), false);
                }
            }
        });

        if (!this.viewModel.getImageDataList().isEmpty()) {
            this.renderFavorites(this.viewModel.getImageDataList(), true);
        }
    }

    private void renderFavorites(List<? extends ImageData> items, Boolean isAdd) {
        if (isAdd) {
            for (ImageData data : items) {
                StackPane card = new StackPane();
                card.getStyleClass().add("card");

                ImageView imageView = new ImageView();
                imageView.getStyleClass().add("card-image");
                imageView.setSmooth(true);
                imageView.setCache(true);
                imageView.setCacheHint(javafx.scene.CacheHint.QUALITY);

                Rectangle clip = new Rectangle(100, 100);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                imageView.setClip(clip);

                StackPane placeholder = new StackPane();
                placeholder.getStyleClass().add("card-placeholder");
                card.getChildren().add(placeholder);

                Task<Image> imgTask = new Task<>() {
                    @Override
                    protected Image call() {
                        return FavoritesPageController.this.viewModel.loadImage(data);
                    }

                    @Override
                    protected void succeeded() {
                        Image image = this.getValue();
                        if (image != null) {
                            card.getChildren().remove(placeholder);
                            card.getChildren().add(imageView);
                            imageView.setImage(image);
                            Store.statusText.set("图片已加载");
                        }
                    }
                };
                ExecutorManager.getPlatformTaskExecutor().execute(imgTask);

                ContextMenu contextMenu = new ContextMenu();
                MenuItem unfavoriteItem = new MenuItem("移除");
                unfavoriteItem.setOnAction(e -> this.viewModel.removeFavorite(data));

                MenuItem copyItem = new MenuItem("复制");
                copyItem.setOnAction(e -> this.viewModel.copyToClipboard(data));

                contextMenu.getItems().addAll(unfavoriteItem, new SeparatorMenuItem(), copyItem);

                card.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        contextMenu.show(card, e.getScreenX(), e.getScreenY());
                    } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                        this.viewModel.copyToClipboard(data);
                    }
                });

                this.favoritesGrid.getChildren().add(card);
            }
        } else {
            List<String> urlList = items.stream()
                    .map(ImageData::getUrl)
                    .toList();
            this.favoritesGrid.getChildren().removeIf(node -> {
                if (node instanceof StackPane stackPane) {
                    for (javafx.scene.Node child : stackPane.getChildren()) {
                        if (child instanceof ImageView imageView && imageView.getImage() != null) {
                            return urlList.contains(imageView.getImage().getUrl());
                        }
                    }
                }
                return false;
            });
        }

    }

    @Override
    public void show() {
        this.favoritesPage.setVisible(true);
        this.favoritesPage.setManaged(true);
    }

    @Override
    public void hide() {
        this.favoritesPage.setVisible(false);
        this.favoritesPage.setManaged(false);
    }

    @Override
    public List<ButtonConfig> getButtonConfigs() {
        return List.of(
                new ButtonConfig(
                        "clearButton",
                        "Clear",
                        "dynamic-btn",
                        event -> {
                            Store.statusText.set("Clearing favorites...");
                            this.viewModel.clearAllFavorites();
                            this.favoritesGrid.getChildren().clear();
                        }
                )
        );
    }
}
