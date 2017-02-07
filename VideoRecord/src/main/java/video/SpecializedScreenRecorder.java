package video;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ConfigUtil.ConfigUtil;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

public class SpecializedScreenRecorder extends ScreenRecorder {

    private String name;
    private String methodName;

    public SpecializedScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
            Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name,
            String methodName) throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
        this.methodName = methodName;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {

        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        VideoReord videoReord = VideoReord.getInstance();
        if (ConfigUtil.getConfigUtil().getConfigFileContent("isVideo").equals("false"))
            videoReord.setCurrentAVIVideoPath(System.getProperty("user.dir") + "/test-output/video/"
                    + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
        else
            videoReord.setCurrentAVIVideoPath(System.getProperty("user.dir") + "/target/surefire-reports/video/"
                    + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
        videoReord.setCurrentWEBMVideoPath(videoReord.getCurrentAVIVideoPath().replace(".avi", ".webm"));
        VideoReord.initVideoPath(methodName, videoReord.getCurrentWEBMVideoPath());
        return new File(movieFolder, videoReord.getCurrentAVIVideoPath());
    }

}