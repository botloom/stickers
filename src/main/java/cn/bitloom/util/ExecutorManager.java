package cn.bitloom.util;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Thread pool manager util.
 *
 * @author bitloom
 */
public class ExecutorManager {

    @Getter
    private static final ExecutorService platformTaskExecutor = Executors.newFixedThreadPool(5);
    @Getter
    private static final ExecutorService watchServiceExecutor = Executors.newSingleThreadExecutor();

    /**
     * Close.
     */
    public static void close(){
        platformTaskExecutor.shutdown();
        watchServiceExecutor.shutdownNow();
    }
}
