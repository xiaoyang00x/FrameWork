package video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ConfigUtil.ConfigUtil;

/**
 * 
 * .avi type video convert to .mp4
 * 
 * @author yangyu
 * 
 */

public class ChangeVideoTool {

    /**
     * @param inputFile:
     *            before video path
     * @param outputFile：finish
     *            video path
     * @return
     */

    public static boolean convert(String inputFile, String outputFile) {
        if (!checkfile(inputFile)) {
            System.out.println(inputFile + " is not a file");
            return false;
        }
        if (checkfile(outputFile)) {
            System.out.println(outputFile + " is already exist");
            return true;
        }

        if (process(inputFile, outputFile)) {
            System.out.println("ok");
            return true;
        }
        return false;
    }

    private static boolean checkfile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * @param inputFile
     * @param outputFile
     * @return success status
     */
    private static boolean process(String inputFile, String outputFile) {
        boolean status = false;
        status = processWEBM(inputFile, outputFile);// 将avi转为flv
        return status;
    }

    // ffmpeg support type ：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv)
    private static boolean processWEBM(String inputFile, String outputFile) {
        if (!checkfile(inputFile)) {
            System.out.println(inputFile + " is not file");
            return false;
        }
        List<String> commend = new ArrayList<String>();
        commend.add(System.getProperty("user.dir")
                + ConfigUtil.getConfigUtil().getConfigFileContent("convertVideoToolMac"));
        commend.add("-i");
        commend.add(inputFile);
        commend.add("-c:v");
        commend.add("libvpx");
        commend.add("-crf");
        commend.add("10");
        commend.add("-b:v");
        commend.add("1M");
        commend.add("-c:a");
        commend.add("libvorbis");
        commend.add(outputFile);
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("CMD: " + test);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process process = builder.start();
            System.out.println("start video convert");
            while (process.isAlive()) {
            }
            System.out.println("finish video convert");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}