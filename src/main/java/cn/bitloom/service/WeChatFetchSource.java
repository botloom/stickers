package cn.bitloom.service;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.enums.ImageFileExtensionEnums;
import cn.bitloom.model.ImageData;
import cn.bitloom.util.BrowserManager;
import cn.bitloom.util.ExecutorManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * The type We chat fetch source.
 */
@Slf4j
public class WeChatFetchSource implements FetchSource {

    private static final String WECHAT_URL = "https://szfilehelper.weixin.qq.com/";
    private static volatile WeChatFetchSource INSTANCE;
    private final Page page;

    private WeChatFetchSource() {
        if (INSTANCE != null) {
            throw new RuntimeException("禁止通过反射创建WeChatFetchSource实例，使用getInstance()获取");
        }
        this.page = BrowserManager.open(WECHAT_URL);
        this.page.onResponse(response -> {
            String url = response.url();
            if (url.contains("webwxgetmsgimg")) {
                try {
                    byte[] imageBytes = this.download(url);
                    Path imagePath = AppConstants.Base.FAVORITES_DIR.resolve("WX_" + UUID.randomUUID() + ImageFileExtensionEnums.GIF.getExtension());
                    Files.write(imagePath, imageBytes);
                } catch (Exception e) {
                    log.error("处理捕获的图片时出错: {}", url, e);
                }
            }
        });
        ExecutorManager.getPlatformTaskExecutor().execute(() -> {
            while (!this.page.isClosed()) {
                try {
                    this.page.waitForTimeout(1000);
                } catch (Exception e) {
                    break;
                }
            }
        });
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static WeChatFetchSource getInstance() {
        if (INSTANCE == null) {
            synchronized (WeChatFetchSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WeChatFetchSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public List<ImageData> fetch(String keyword, int cursor) {
        throw new RuntimeException("暂不支持该操作");
    }

    @Override
    public byte[] download(String imageUrl) {
        return this.page.request().get(imageUrl).body();
    }

    @Override
    public boolean isReady() {
        return false;
    }
}