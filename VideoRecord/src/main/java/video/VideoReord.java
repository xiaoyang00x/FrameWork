package video;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

public class VideoReord {

    private static VideoReord instance = null;

    private static List<String> videoPath = new ArrayList<String>();

    private ScreenRecorder screenRecorder;

    private static String currentAVIVideoPath;

    private static String currentWEBMVideoPath;

    private VideoReord() {

    }

    public static VideoReord getInstance() {
        if (instance == null) {
            synchronized (VideoReord.class) {
                if (instance == null) {
                    instance = new VideoReord();
                    return instance;
                }
            }
        }
        return instance;
    }

    public static void initVideoPath(String path) {
        getInstance().getVideoPath().add(path);
    }

    public static List<String> getVideoPath() {
        return videoPath;
    }

    public static void setVideoPath(List<String> videoPath) {
        VideoReord.videoPath = videoPath;
    }

    public void startRecording() throws Exception {
        File file = new File("");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        Rectangle captureSize = new Rectangle(0, 0, width, height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();

        this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                        Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                null, file, "");
        this.screenRecorder.start();

    }

    public void stopRecording() throws Exception {
        this.screenRecorder.stop();
        ChangeVideoTool.convert(this.currentAVIVideoPath, this.currentWEBMVideoPath);
    }

    public ScreenRecorder getScreenRecorder() {
        return screenRecorder;
    }

    public void setScreenRecorder(ScreenRecorder screenRecorder) {
        this.screenRecorder = screenRecorder;
    }

    public static String getCurrentAVIVideoPath() {
        return currentAVIVideoPath;
    }

    public static void setCurrentAVIVideoPath(String currentAVIVideoPath) {
        VideoReord.currentAVIVideoPath = currentAVIVideoPath;
    }

    public static String getCurrentWEBMVideoPath() {
        return currentWEBMVideoPath;
    }

    public static void setCurrentWEBMVideoPath(String currentWEBMVideoPath) {
        VideoReord.currentWEBMVideoPath = currentWEBMVideoPath;
    }

    public static void setInstance(VideoReord instance) {
        VideoReord.instance = instance;
    }

}