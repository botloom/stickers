module cn.bitloom.stickers {
    requires javafx.controls;
    requires javafx.fxml;
    requires playwright;
    requires com.alibaba.fastjson2;
    requires java.desktop;
    requires org.slf4j;
    requires javafx.graphics;
    requires javafx.base;
    requires static lombok;
    requires animated.gif.lib;

    exports cn.bitloom;
    exports cn.bitloom.constant;
    exports cn.bitloom.controller;
    exports cn.bitloom.model;
    exports cn.bitloom.router;
    exports cn.bitloom.store;
    exports cn.bitloom.util;
    exports cn.bitloom.vm;
    exports cn.bitloom.enums;

    opens cn.bitloom to javafx.fxml;
    opens cn.bitloom.controller to javafx.fxml;
    opens cn.bitloom.model to javafx.fxml;
    opens cn.bitloom.vm to javafx.fxml;
    exports cn.bitloom.holder;
    opens cn.bitloom.holder to javafx.fxml;
    exports cn.bitloom.service;
}
