package cn.bitloom.util;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * The type Image util.
 *
 * @author bitloom
 */
@Slf4j
public class ImageUtil {

    /**
     * Webp to gif.
     *
     * @param webpBytes the webp bytes
     * @param gifPath   the gif path
     * @throws IOException the io exception
     */
    public static void webpToGif(byte[] webpBytes, String gifPath) throws IOException {
        try (InputStream is = new ByteArrayInputStream(webpBytes)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("webp");
            if (!readers.hasNext()) {
                throw new IOException("No WebP image reader available");
            }

            ImageReader reader = readers.next();
            try (ImageInputStream iis = ImageIO.createImageInputStream(is);
                 FileOutputStream fos = new FileOutputStream(gifPath)) {

                reader.setInput(iis);

                int frameCount = reader.getNumImages(true);
                if (frameCount == 0) {
                    throw new IOException("No frames found in WebP file");
                }

                AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
                gifEncoder.start(fos);
                gifEncoder.setRepeat(0);
                gifEncoder.setQuality(15);

                for (int i = 0; i < frameCount; i++) {
                    BufferedImage frame = reader.read(i);
                    IIOMetadata metadata = reader.getImageMetadata(i);

                    int delay = getDelayFromMetadata(metadata);

                    gifEncoder.setDelay(delay);
                    gifEncoder.addFrame(frame);

                    frame.flush();
                }

                gifEncoder.finish();
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * Webp to png.
     *
     * @param webpBytes the webp bytes
     * @param pngPath   the png path
     * @throws IOException the io exception
     */
    public static void webpToPng(byte[] webpBytes, String pngPath) throws IOException {
        try (InputStream is = new ByteArrayInputStream(webpBytes)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("webp");
            if (!readers.hasNext()) {
                throw new IOException("No WebP image reader available");
            }

            ImageReader reader = readers.next();
            try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
                reader.setInput(iis);

                BufferedImage image = reader.read(0);
                ImageIO.write(image, "png", new File(pngPath));

                image.flush();
            } finally {
                reader.dispose();
            }
        }
    }

    // 修改后的方法，需要传入 reader 来获取正确的格式名称
    private static int getDelayFromMetadata(IIOMetadata metadata) {
        try {
            String nativeFormat = metadata.getNativeMetadataFormatName();
            
            if (nativeFormat == null || nativeFormat.isEmpty()) {
                log.warn("无法获取原生元数据格式名称，使用默认延迟");
                return 100;
            }

            Node root = metadata.getAsTree(nativeFormat);

            Node durationNode = findNamedNode(root, "Duration");

            if (durationNode != null) {
                String delayStr = durationNode.getNodeValue();
                if (delayStr != null && !delayStr.isEmpty()) {
                    int delay = Integer.parseInt(delayStr);
                    return delay <= 10 ? 100 : delay;
                }
            }
        } catch (Exception e) {
            log.error("解析元数据失败", e);
        }
        return 100;
    }

    // 通用的递归查找节点方法，比写死的getChildNode更健壮
    private static Node findNamedNode(Node root, String name) {
        if (root == null) return null;

        if (name.equalsIgnoreCase(root.getNodeName())) {
            return root;
        }

        // 检查属性 (有时候值在属性里，比如 <Node Duration="100">)
        if (root.getAttributes() != null) {
            for (int i = 0; i < root.getAttributes().getLength(); i++) {
                Node attr = root.getAttributes().item(i);
                if (name.equalsIgnoreCase(attr.getNodeName())) {
                    return attr;
                }
            }
        }

        // 递归子节点
        Node child = root.getFirstChild();
        while (child != null) {
            Node found = findNamedNode(child, name);
            if (found != null) return found;
            child = child.getNextSibling();
        }
        return null;
    }
}