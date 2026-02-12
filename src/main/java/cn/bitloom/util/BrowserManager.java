package cn.bitloom.util;

import cn.bitloom.constant.AppConstants;
import cn.bitloom.store.Store;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * The type Browser model.
 *
 * @author bitloom
 */
@Slf4j
public class BrowserManager {

    private static final Playwright playwright;
    private static final Process chromeProcess;
    private static final Browser browser;
    private static final BrowserContext context;

    static {
        try {
            playwright = Playwright.create();
            int port = 9222;
            ProcessBuilder pb = new ProcessBuilder(
                    Store.browserPath.get().toString(),
                    "--remote-debugging-port=9222",
                    "--user-data-dir=" + AppConstants.Base.PLAYWRIGHT_DIR
            );
            pb.redirectErrorStream(true);
            chromeProcess = pb.start();
            browser = playwright.chromium().connectOverCDP("http://localhost:" + port);
            context = browser.contexts().isEmpty() ? browser.newContext() : browser.contexts().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BrowserManager() {
    }

    /**
     * Open page.
     *
     * @param url the url
     * @return the page
     */
    public static Page open(String url) {
        Page page;
        page = context.newPage();
        page.navigate(url);
        return page;
    }

    /**
     * Close.
     */
    public static void close() {
        if (Objects.nonNull(context)) {
            context.close();
        }
        if (Objects.nonNull(browser)) {
            browser.close();
        }
        if (Objects.nonNull(chromeProcess)) {
            chromeProcess.destroyForcibly();
        }
        if (Objects.nonNull(playwright)) {
            playwright.close();
        }
    }

}
