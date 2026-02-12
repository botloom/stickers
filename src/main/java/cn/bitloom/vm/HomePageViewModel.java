package cn.bitloom.vm;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.enums.ImageFileExtensionEnums;
import cn.bitloom.model.ImageData;
import cn.bitloom.service.FetchSource;
import cn.bitloom.store.Store;
import cn.bitloom.util.ExecutorManager;
import cn.bitloom.util.ImageUtil;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * The type Main container view model.
 *
 * @author bitloom
 */
@Slf4j
public class HomePageViewModel {

    @Getter
    private final StringProperty searchTextProperty = new SimpleStringProperty("");
    @Getter
    private final StringProperty currentKeyword = new SimpleStringProperty("");
    @Getter
    private final BooleanProperty hasMore = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    @Getter
    private final IntegerProperty currentCursor = new SimpleIntegerProperty(0);
    @Getter
    private final ObservableList<ImageData> imageDataList = FXCollections.observableArrayList();

    public void searchStickers() {
        if (this.searchTextProperty.isEmpty().get()) {
            Store.statusText.set("请输入搜索关键词");
            return;
        }
        String searchText = this.searchTextProperty.get();
        this.currentCursor.set(0);
        this.hasMore.set(false);
        this.currentKeyword.set(searchText);
        this.imageDataList.clear();
        Store.statusText.set("正在搜索...");
        this.isLoading.set(true);
        this.search(false);
    }

    public void hasMore() {
        if (!this.hasMore.get() || this.currentKeyword.isEmpty().get()) {
            return;
        }
        this.currentCursor.set(this.currentCursor.get() + 50);
        this.isLoading.set(true);
        this.search(true);
    }

    private void search(Boolean isAppend) {
        Task<ObservableList<ImageData>> task = new Task<>() {
            @Override
            protected ObservableList<ImageData> call() {
                ObservableList<ImageData> list = FXCollections.observableArrayList();
                List<FetchSource> fetchSourceList = Store.selectedFetchSources.stream()
                        .map(selectedFetchSource -> selectedFetchSource.getFetchSourceInstance().get())
                        .filter(FetchSource::isReady)
                        .toList();
                fetchSourceList.forEach(fetchSource -> {
                    List<ImageData> results = fetchSource.fetch(HomePageViewModel.this.searchTextProperty.get(), HomePageViewModel.this.currentCursor.get());
                    list.addAll(results);
                });
                boolean hasMore = fetchSourceList.stream()
                        .anyMatch(fs -> fs.hasMore(HomePageViewModel.this.searchTextProperty.get(), HomePageViewModel.this.currentCursor.get()));
                HomePageViewModel.this.hasMore.set(hasMore);
                return list;
            }

            @Override
            protected void succeeded() {
                ObservableList<ImageData> result = this.getValue();
                int currentCount = HomePageViewModel.this.imageDataList.size();
                int totalCount = currentCount + result.size();
                if (isAppend) {
                    HomePageViewModel.this.imageDataList.addAll(result);
                    Store.statusText.set(totalCount + " 项");
                } else {
                    HomePageViewModel.this.imageDataList.setAll(result);
                    Store.statusText.set(result.size() + " 项");
                }
                HomePageViewModel.this.isLoading.set(false);
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                String errorMsg = exception.getMessage();
                if (errorMsg != null && errorMsg.contains("HTTP Error: 401")) {
                    Store.statusText.set("搜索失败: 需要登录抖音账号");
                } else if (errorMsg != null && errorMsg.contains("HTTP Error: 403")) {
                    Store.statusText.set("搜索失败: 触发了风控限制");
                } else {
                    Store.statusText.set("搜索失败: " + (errorMsg != null ? errorMsg : "未知错误"));
                }
                HomePageViewModel.this.isLoading.set(false);
            }
        };

        ExecutorManager.getPlatformTaskExecutor().execute(task);
    }

    /**
     * Load static image image.
     *
     * @param data the data
     * @return the image
     */
    public Image loadImage(ImageData data) {
        try {
            if (data.getFileExtension() == ImageFileExtensionEnums.A_WEBP) {
                Path imagePath = AppConstants.Base.TEMP_DIR.resolve(data.getKeyword() + "_" + UUID.randomUUID() + ImageFileExtensionEnums.GIF.getExtension());
                byte[] imageBytes = data.getFetchSource().download(data.getUrl());
                ImageUtil.webpToGif(imageBytes, imagePath.toString());
                data.setPath(imagePath);
                return new Image(imagePath.toUri().toURL().toString());
            } else {
                Path imagePath;
                if (data.getFileExtension() == ImageFileExtensionEnums.WEBP) {
                    imagePath = AppConstants.Base.TEMP_DIR.resolve(data.getKeyword() + "_" + UUID.randomUUID() + ImageFileExtensionEnums.PNG.getExtension());
                } else {
                    imagePath = AppConstants.Base.TEMP_DIR.resolve(data.getKeyword() + "_" + UUID.randomUUID() + data.getFileExtension().getExtension());
                }
                byte[] imageBytes = data.getFetchSource().download(data.getUrl());
                Files.write(imagePath, imageBytes);
                data.setPath(imagePath);
                return new Image(data.getPath().toUri().toURL().toString());
            }
        } catch (Exception e) {
            log.error("图片加载失败", e);
        }
        return null;
    }

    public void copyToClipboard(ImageData data) {
        try {
            List<File> fileList = List.of(data.getPath().toFile());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            Transferable transferable = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.javaFileListFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                        return fileList;
                    }
                    throw new UnsupportedFlavorException(flavor);
                }
            };

            clipboard.setContents(transferable, null);
            Store.statusText.set("已复制");
        } catch (Exception e) {
            log.error("Failed to copy file to clipboard", e);
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        this.imageDataList.clear();
        this.currentCursor.set(0);
        this.currentKeyword.set("");
        this.searchTextProperty.set("");
        this.hasMore.set(false);
    }
}
