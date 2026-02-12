package cn.bitloom.controller;

import cn.bitloom.holder.PageHolder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class TagsPageController implements Initializable, PageHolder {

    @FXML
    private VBox tagsPage;

    @Getter
    @Setter
    private IndexController indexController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.hide();
    }

    @Override
    public void show() {
        this.tagsPage.setVisible(true);
        this.tagsPage.setManaged(true);
    }

    @Override
    public void hide() {
        this.tagsPage.setVisible(false);
        this.tagsPage.setManaged(false);
    }
}
