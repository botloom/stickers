package cn.bitloom.service;

import cn.bitloom.enums.ImageFileExtensionEnums;
import cn.bitloom.model.ImageData;
import cn.bitloom.util.BrowserManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DouyinFetchSource implements FetchSource {

    private static volatile DouyinFetchSource INSTANCE;
    private final Page page;

    private DouyinFetchSource() {
        if (INSTANCE != null) {
            throw new RuntimeException("禁止通过反射创建DouyinModel实例，使用getInstance()获取");
        }
        this.page = BrowserManager.open("https://www.douyin.com");
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static DouyinFetchSource getInstance() {
        if (INSTANCE == null) {
            synchronized (DouyinFetchSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DouyinFetchSource();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Search stickers list.
     *
     * @param keyword the keyword
     * @param cursor  the cursor
     * @return the list
     */
    public List<ImageData> searchStickers(String keyword, int cursor) {
        if (this.page == null || this.page.isClosed()) {
            log.error("浏览器页面未就绪");
            return new ArrayList<>();
        }

        try {
            String url = "https://www.douyin.com/aweme/v1/web/im/resource/emoticon/search/?keyword="
                    + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                    + "&count=50&cursor=" + cursor;

            APIResponse response = this.page.request().fetch(url, RequestOptions.create()
                    .setMethod("GET")
                    .setHeader("Referer", "https://www.douyin.com/")
                    .setHeader("Accept", "application/json")
            );

            if (response.ok()) {
                String body = response.text();
                JSONObject root = JSON.parseObject(body);

                if (root != null && root.containsKey("emoticon_data")) {
                    JSONObject emoticonData = root.getJSONObject("emoticon_data");

                    if (emoticonData.containsKey("sticker_list")) {
                        JSONArray stickers = emoticonData.getJSONArray("sticker_list");

                        List<ImageData> list = new ArrayList<>();
                        if (stickers != null) {
                            for (int i = 0; i < stickers.size(); i++) {
                                JSONObject item = stickers.getJSONObject(i);
                                JSONObject origin = item.getJSONObject("origin");
                                if (origin != null && origin.containsKey("url_list")) {
                                    JSONArray urlList = origin.getJSONArray("url_list");
                                    if (urlList != null && !urlList.isEmpty()) {
                                        String imgUrl = urlList.getString(0);
                                        ImageFileExtensionEnums fileExtension = ImageFileExtensionEnums.getByUrl(imgUrl);
                                        list.add(new ImageData(imgUrl, fileExtension, keyword, null, this));
                                    }
                                }
                            }
                        }
                        return list;
                    }
                }
            } else {
                String errorBody = response.text();
                log.error("抖音搜索HTTP错误: {} - {}", response.status(), errorBody.length() > 100 ? errorBody.substring(0, 100) : errorBody);
            }
        } catch (Exception e) {
            log.error("抖音搜索失败", e);
        }

        return new ArrayList<>();
    }

    /**
     * Has more boolean.
     *
     * @param keyword the keyword
     * @param cursor  the cursor
     * @return the boolean
     */
    public boolean hasMore(String keyword, int cursor) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }

        if (this.page == null || this.page.isClosed()) {
            return false;
        }

        try {
            String url = "https://www.douyin.com/aweme/v1/web/im/resource/emoticon/search/?keyword="
                    + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                    + "&count=50&cursor=" + cursor;

            APIResponse response = this.page.request().fetch(url, RequestOptions.create()
                    .setMethod("GET")
                    .setHeader("Referer", "https://www.douyin.com/")
            );

            if (response.ok()) {
                String body = response.text();
                JSONObject root = JSON.parseObject(body);

                if (root != null && root.containsKey("emoticon_data")) {
                    JSONObject emoticonData = root.getJSONObject("emoticon_data");
                    boolean hasMore = emoticonData.getBooleanValue("has_more");

                    if (emoticonData.containsKey("sticker_list")) {
                        JSONArray stickers = emoticonData.getJSONArray("sticker_list");
                        return hasMore || (stickers != null && stickers.size() >= 50);
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查是否有更多数据失败", e);
        }

        return false;
    }

    /**
     * Download image byte [ ].
     * 使用 Playwright 原生请求下载，避免 JS 注入和 Base64 转换开销。
     *
     * @param imageUrl the image url
     * @return the byte [ ]
     * @throws Exception the exception
     */
    public byte[] download(String imageUrl) throws Exception {
        if (this.page == null || this.page.isClosed()) {
            throw new Exception("浏览器页面未就绪");
        }

        try {
            APIResponse response = this.page.request().fetch(imageUrl);

            if (response.ok()) {
                return response.body();
            } else {
                throw new Exception("下载图片失败，状态码: " + response.status());
            }
        } catch (Exception e) {
            throw new Exception("下载图片异常: " + e.getMessage(), e);
        }
    }

    /**
     * Is ready boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isReady() {
        return this.page != null && !this.page.isClosed();
    }

    @Override
    public List<ImageData> fetch(String keyword, int cursor) {
        return searchStickers(keyword, cursor);
    }
}