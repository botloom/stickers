package cn.bitloom.model;

import cn.bitloom.enums.ImageFileExtensionEnums;
import cn.bitloom.service.FetchSource;
import lombok.Data;

import java.nio.file.Path;

/**
 * The type Image data.
 *
 * @author bitloom
 */
@Data
public class ImageData {
    private String url;
    private ImageFileExtensionEnums fileExtension;
    private String keyword;
    private Path path;
    private FetchSource fetchSource;
    private String id;

    public ImageData(String url, ImageFileExtensionEnums fileExtension, String keyword, Path path, FetchSource fetchSource) {
        this.url = url;
        this.fileExtension = fileExtension;
        this.keyword = keyword;
        this.path = path;
        this.fetchSource = fetchSource;
        this.id = url;
    }
}