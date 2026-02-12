package cn.bitloom.vm;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.enums.ImageFileExtensionEnums;
import cn.bitloom.model.ImageData;
import cn.bitloom.store.Store;
import cn.bitloom.util.ExecutorManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * The type Favorites page view model.
 *
 * @author bitloom
 */
@Slf4j
public class FavoritesPageViewModel {

    @Getter
    private final ObservableList<ImageData> imageDataList = FXCollections.observableArrayList();

    public FavoritesPageViewModel() {
        this.loadFavorites();
        this.startWatchService();
    }

    private void loadFavorites() {
        try (Stream<Path> pathStream = Files.list(AppConstants.Base.FAVORITES_DIR)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String fileName = path.getFileName().toString();
                            String keyword = fileName.substring(0, fileName.lastIndexOf('_'));
                            String extension = fileName.substring(fileName.lastIndexOf('.'));

                            String fileExtension = extension.substring(1).toUpperCase();
                            ImageFileExtensionEnums imageFileExtension = ImageFileExtensionEnums.getByExtension(fileExtension);
                            ImageData imageData = new ImageData(
                                    path.toUri().toURL().toString(),
                                    imageFileExtension,
                                    keyword,
                                    path,
                                    null
                            );
                            this.imageDataList.add(imageData);
                        } catch (Exception e) {
                            log.error("加载收藏图片失败: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("加载收藏数据失败", e);
        }
    }

    public void addToFavorites(ImageData imageData) {
        try {
            if (imageData.getPath() == null || !Files.exists(imageData.getPath())) {
                Store.statusText.set("图片未加载，无法收藏");
                return;
            }

            String originalFileName = imageData.getPath().getFileName().toString();
            Path targetPath = AppConstants.Base.FAVORITES_DIR.resolve(originalFileName);

            Files.copy(imageData.getPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String keyword = originalFileName.substring(0, originalFileName.lastIndexOf('_'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String fileExtension = extension.substring(1).toUpperCase();
            ImageFileExtensionEnums imageFileExtension = ImageFileExtensionEnums.getByExtension(fileExtension);

            ImageData favoriteImageData = new ImageData(
                    targetPath.toUri().toURL().toString(),
                    imageFileExtension,
                    keyword,
                    targetPath,
                    null
            );
            this.imageDataList.add(favoriteImageData);
            Store.statusText.set("已收藏");
        } catch (Exception e) {
            log.error("收藏失败", e);
            Store.statusText.set("收藏失败");
        }
    }

    public void removeFavorite(ImageData imageData) {
        try {
            if (imageData.getPath() != null && Files.exists(imageData.getPath())) {
                Files.delete(imageData.getPath());
            }
        } catch (Exception e) {
            log.error("删除收藏图片失败", e);
        }
        this.imageDataList.removeIf(f -> f.getId().equals(imageData.getId()));
    }

    public void clearAllFavorites() {
        for (ImageData imageData : this.imageDataList) {
            try {
                if (imageData.getPath() != null && Files.exists(imageData.getPath())) {
                    Files.delete(imageData.getPath());
                }
            } catch (Exception e) {
                log.error("删除收藏图片失败", e);
            }
        }
        this.imageDataList.clear();
    }

    public Image loadImage(ImageData data) {
        try {
            if (data.getPath() != null && Files.exists(data.getPath())) {
                return new Image(data.getPath().toUri().toURL().toString());
            }
            return null;
        } catch (Exception e) {
            log.error("图片加载失败", e);
        }
        return null;
    }

    public void copyToClipboard(ImageData data) {
        try {
            java.util.List<File> fileList = java.util.List.of(data.getPath().toFile());
            java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
            java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();

            java.awt.datatransfer.Transferable transferable = new java.awt.datatransfer.Transferable() {
                @Override
                public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
                    return new java.awt.datatransfer.DataFlavor[]{java.awt.datatransfer.DataFlavor.javaFileListFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) {
                    return java.awt.datatransfer.DataFlavor.javaFileListFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException {
                    if (java.awt.datatransfer.DataFlavor.javaFileListFlavor.equals(flavor)) {
                        return fileList;
                    }
                    throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
                }
            };

            clipboard.setContents(transferable, null);
            Store.statusText.set("已复制");
        } catch (Exception e) {
            log.error("Failed to copy file to clipboard", e);
        }
    }

    private void startWatchService() {
        ExecutorManager.getWatchServiceExecutor().submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                AppConstants.Base.FAVORITES_DIR.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE);

                while (true) {
                    WatchKey key = watchService.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                    if (key != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();

                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            }

                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path filename = ev.context();
                            Path fullPath = AppConstants.Base.FAVORITES_DIR.resolve(filename);

                            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                handleNewFile(fullPath);
                            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                handleDeletedFile(fullPath);
                            }
                        }
                        boolean valid = key.reset();
                        if (!valid) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                if (!(e instanceof InterruptedException)) {
                    log.error("监听文件夹失败", e);
                }
            }
        });
    }

    private void handleNewFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }

        try {
            String fileName = path.getFileName().toString();
            String keyword = fileName.substring(0, fileName.lastIndexOf('_'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));

            String fileExtension = extension.substring(1).toUpperCase();
            ImageFileExtensionEnums imageFileExtension = ImageFileExtensionEnums.getByExtension(fileExtension);
            ImageData imageData = new ImageData(
                    path.toUri().toURL().toString(),
                    imageFileExtension,
                    keyword,
                    path,
                    null
            );

            javafx.application.Platform.runLater(() -> {
                if (this.imageDataList.stream().noneMatch(d -> d.getPath().equals(path))) {
                    this.imageDataList.add(imageData);
                }
            });
        } catch (Exception e) {
            log.error("处理新文件失败: {}", path, e);
        }
    }

    private void handleDeletedFile(Path path) {
        javafx.application.Platform.runLater(() -> {
            this.imageDataList.removeIf(d -> d.getPath() != null && d.getPath().equals(path));
        });
    }

}