package cn.bitloom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * The enum Image file extension enums.
 *
 * @author bitloom
 */
@Getter
@RequiredArgsConstructor
public enum ImageFileExtensionEnums {
    /**
     * Webp image file extension enums.
     */
    WEBP(".webp"),
    /**
     * A webp image file extension enums.
     */
    A_WEBP(".awebp"),
    /**
     * Gif image file extension enums.
     */
    GIF(".gif"),
    /**
     * Png image file extension enums.
     */
    PNG(".png"),
    /**
     * Jpeg image file extension enums.
     */
    JPEG(".jpeg"),
    /**
     * Jpg image file extension enums.
     */
    JPG(".jpg");
    private final String extension;

    /**
     * Gets by url.
     *
     * @param url the url
     * @return the by url
     */
    public static ImageFileExtensionEnums getByUrl(String url) {
        if (Objects.isNull(url)) {
            throw new IllegalArgumentException("url is null");
        }
        if (url.toLowerCase().contains("awebp")) {
            return A_WEBP;
        } else if (url.toLowerCase().contains("webp")) {
            return WEBP;
        } else if (url.toLowerCase().contains("gif")) {
            return GIF;
        } else if (url.toLowerCase().contains("png")) {
            return PNG;
        } else if (url.toLowerCase().contains("jpeg")) {
            return JPEG;
        } else if (url.toLowerCase().contains("jpg")) {
            return JPG;
        } else {
            throw new IllegalArgumentException("Invalid URL");
        }
    }

    /**
     * Gets by extension.
     *
     * @param extension the extension
     * @return the by extension
     */
    public static ImageFileExtensionEnums getByExtension(String extension) {
        if (Objects.isNull(extension)) {
            throw new IllegalArgumentException("extension is null");
        }
        for (ImageFileExtensionEnums imageFileExtension : ImageFileExtensionEnums.values()) {
            if (imageFileExtension.getExtension().equals(extension)) {
                return imageFileExtension;
            }
        }
        return null;
    }
}
