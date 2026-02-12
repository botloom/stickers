package cn.bitloom.service;

import cn.bitloom.model.ImageData;

import java.util.List;

/**
 * The interface Fetch source.
 *
 * @author bitloom
 */
public interface FetchSource {
    /**
     * Search list.
     *
     * @param keyword the keyword
     * @param cursor  the cursor
     * @return the list
     */
    List<ImageData> fetch(String keyword, int cursor);

    /**
     * Is ready boolean.
     *
     * @return the boolean
     */
    boolean isReady();

    /**
     * Has more boolean.
     *
     * @param keyword the keyword
     * @param cursor  the cursor
     * @return the boolean
     */
    default boolean hasMore(String keyword, int cursor) {
        return false;
    }

    /**
     * Download image byte [ ].
     *
     * @param imageUrl the image url
     * @return the byte [ ]
     * @throws Exception the exception
     */
    default byte[] download(String imageUrl) throws Exception {
        throw new UnsupportedOperationException("Download not supported");
    }

}
