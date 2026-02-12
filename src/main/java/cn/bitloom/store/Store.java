package cn.bitloom.store;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.enums.FetchSourceEnums;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The type Store.
 *
 * @author bitloom
 */
public class Store {
    /**
     * The constant stageHeight.
     */
    public static final DoubleProperty stageHeight = new SimpleDoubleProperty();
    /**
     * The constant stageWidth.
     */
    public static final DoubleProperty stageWidth = new SimpleDoubleProperty();
    /**
     * The constant statusText.
     */
    public static final StringProperty statusText = new SimpleStringProperty();
    /**
     * The constant savePath.
     */
    public static final ObjectProperty<Path> savePath = new SimpleObjectProperty<>(AppConstants.Base.APP_DIR.resolve("outputs"));
    /**
     * The constant browserPath.
     */
    public static final ObjectProperty<Path> browserPath = new SimpleObjectProperty<>(Paths.get("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"));
    /**
     * The constant selectedDataSources.
     */
    public static final ObservableList<FetchSourceEnums> selectedFetchSources = FXCollections.observableArrayList(FetchSourceEnums.DOUYIN);

    static {
        selectedFetchSources.addListener((ListChangeListener<? super FetchSourceEnums>) (change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(selectedFetchSource -> selectedFetchSource.getFetchSourceInstance().get());
                }
            }
        });
    }
}