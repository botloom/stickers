package cn.bitloom.controller;

import cn.bitloom.holder.ButtonBarHolder;
import cn.bitloom.holder.PageHolder;
import cn.bitloom.model.ImageData;
import cn.bitloom.store.Store;
import cn.bitloom.util.ExecutorManager;
import cn.bitloom.vm.HomePageViewModel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The type Home page controller.
 *
 * @author bitloom
 */
@Slf4j
public class HomePageController implements Initializable, ButtonBarHolder, PageHolder {

    @FXML
    private VBox homePage;
    @FXML
    private VBox searchBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private VBox icon;
    @FXML
    @Getter
    private FlowPane gridPane;
    @FXML
    private ScrollPane scrollPane;

    @Getter
    private HomePageViewModel viewModel;

    @Getter
    @Setter
    private IndexController indexController;

    private boolean isSearching = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.viewModel = new HomePageViewModel();
        this.viewModel.getSearchTextProperty().bind(this.searchField.textProperty());

        this.searchField.setOnAction(event -> this.handleSearch());
        this.searchButton.setOnAction(event -> this.handleSearch());

        this.scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() == 1.0 && this.viewModel.getHasMore().get() && !this.viewModel.getIsLoading().get()) {
                this.viewModel.hasMore();
            }
        });

        this.viewModel.getImageDataList().addListener((ListChangeListener<? super ImageData>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    int startIndex = this.viewModel.getImageDataList().size() - change.getAddedSize();
                    this.renderImages(change.getAddedSubList(), startIndex);
                }
                if (change.wasRemoved()) {
                    this.gridPane.getChildren().clear();
                }
            }
        });

        this.viewModel.getIsLoading().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Store.statusText.set("正在加载...");
            } else {
                Store.statusText.set("就绪");
            }
        });

    }

    private void handleSearch() {
        if (this.isSearching) {
            return;
        }
        this.isSearching = true;
        Store.statusText.set("正在搜索...");
        this.viewModel.searchStickers();
        this.animateToSearchState();
    }

    private void animateToSearchState() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(600),
                new KeyValue(this.icon.opacityProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(this.icon.translateYProperty(), -60, Interpolator.EASE_BOTH)
        );

        timeline.getKeyFrames().add(keyFrame);

        timeline.setOnFinished(event -> {
            this.icon.setVisible(false);
            this.icon.setManaged(false);

            this.homePage.setAlignment(Pos.BOTTOM_CENTER);

            VBox.setMargin(this.searchBox, new Insets(0, 0, 15, 0));
            this.scrollPane.setVisible(true);
            this.scrollPane.setManaged(true);

            this.isSearching = false;
        });

        timeline.play();
    }

    /**
     * Render images.
     *
     * @param items      the items
     * @param startIndex the start index
     */
    public void renderImages(List<? extends ImageData> items, int startIndex) {
        Store.statusText.set("正在渲染 " + items.size() + " 张图片...");

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
                    return HomePageController.this.viewModel.loadImage(data);
                }

                @Override
                protected void succeeded() {
                    Image image = this.getValue();
                    card.getChildren().remove(placeholder);
                    card.getChildren().add(imageView);
                    imageView.setImage(image);
                    Store.statusText.set("图片已加载");
                }
            };
            ExecutorManager.getPlatformTaskExecutor().execute(imgTask);

            ContextMenu contextMenu = new ContextMenu();
            MenuItem favoriteItem = new MenuItem("收藏");
            favoriteItem.setOnAction(e -> {
                if (HomePageController.this.indexController != null) {
                    FavoritesPageController favoritesPageController = HomePageController.this.indexController.getFavoritesPageController();
                    if (favoritesPageController != null) {
                        favoritesPageController.getViewModel().addToFavorites(data);
                    }
                }
            });

            MenuItem copyItem = new MenuItem("复制");
            copyItem.setOnAction(e -> HomePageController.this.viewModel.copyToClipboard(data));

            contextMenu.getItems().addAll(favoriteItem, new SeparatorMenuItem(), copyItem);

            card.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(card, e.getScreenX(), e.getScreenY());
                } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    HomePageController.this.viewModel.copyToClipboard(data);
                }
            });

            HomePageController.this.gridPane.getChildren().add(card);
        }
    }

    /**
     * Show main content.
     */
    @Override
    public void show() {
        homePage.setVisible(true);
        homePage.setManaged(true);
    }

    /**
     * Hide main content.
     */
    @Override
    public void hide() {
        homePage.setVisible(false);
        homePage.setManaged(false);
    }

    @Override
    public List<ButtonBarHolder.ButtonConfig> getButtonConfigs() {
        return List.of(
                new ButtonBarHolder.ButtonConfig(
                        "clearButton",
                        "清除",
                        "dynamic-btn",
                        event -> {
                            Store.statusText.set("正在清除结果...");
                            this.gridPane.getChildren().clear();
                            this.searchField.setText("");

                            this.homePage.setAlignment(Pos.CENTER);
                            VBox.setMargin(this.searchBox, new Insets(0, 0, 0, 0));
                            this.scrollPane.setVisible(false);
                            this.scrollPane.setManaged(false);

                            this.icon.setVisible(true);
                            this.icon.setManaged(true);
                            this.icon.setOpacity(0);
                            this.icon.setTranslateY(-60);

                            Timeline timeline = new Timeline();
                            KeyFrame keyFrame = new KeyFrame(Duration.millis(600),
                                    new KeyValue(this.icon.opacityProperty(), 1, Interpolator.EASE_BOTH),
                                    new KeyValue(this.icon.translateYProperty(), 0, Interpolator.EASE_BOTH)
                            );

                            timeline.getKeyFrames().add(keyFrame);
                            timeline.setOnFinished(e -> Store.statusText.set("就绪"));
                            timeline.play();
                        }
                )
        );
    }

}