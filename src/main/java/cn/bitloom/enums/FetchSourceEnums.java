package cn.bitloom.enums;

import cn.bitloom.service.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 * The enum Fetch data source enums.
 *
 * @author bitloom
 */
@Getter
@RequiredArgsConstructor
public enum FetchSourceEnums {
    /**
     * Douyin fetch data source enums.
     */
    DOUYIN("抖音", "从抖音平台搜索表情包，需要 Playwright 浏览器", DouyinFetchSource::getInstance),
    /**
     * We chat fetch data source enums.
     */
    WE_CHAT("微信", "从微信平台搜索表情包，需要 Playwright 浏览器", WeChatFetchSource::getInstance),
    ;
    private final String name;
    private final String desc;
    private final Supplier<FetchSource> fetchSourceInstance;


}
