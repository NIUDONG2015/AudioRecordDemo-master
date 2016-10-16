package zd.audiorecordTest.audio;

/**

 */

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioFileFunc {

    // /storage/emulated/0/AudioTalk/10-15_23-16-48.raw
    private static String LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH-mm-ss");// log
    //音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    //采用频率 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public final static int AUDIO_SAMPLE_RATE = 22050;  //44.1KHz,普遍使用的频率

    //单双声道 MONO:单声道 ， STEREO:双通道
    public final static int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    //音频格式 PCM 16bit分辨率
    public final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    //录音输出文件
    private final static String AUDIO_RAW_FILENAME = sdf.format(new Date()) + ".raw";
    private final static String AUDIO_WAV_FILENAME = sdf.format(new Date()) + ".wav";


    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取麦克风输入的原始音频流文件路径
     *
     * @return
     */
    public static String getRawFilePath() {

        String mAudioRawPath = "";
        if (isSdcardExit()) {
            mAudioRawPath = LOG_PATH_SDCARD_DIR + File.separator + "AudioTalk" + File.separator + AUDIO_RAW_FILENAME;
        }

        return mAudioRawPath;
    }

    /**
     * 获取编码后的WAV格式音频文件路径
     *
     * @return
     */
    public static String getWavFilePath() {
        String mAudioWavPath = "";
        if (isSdcardExit()) {
            mAudioWavPath = LOG_PATH_SDCARD_DIR + File.separator + "AudioTalk" + File.separator + AUDIO_WAV_FILENAME;
        }
        return mAudioWavPath;
    }


    /**
     * 获取文件大小
     *
     * @param path,文件的绝对路径
     * @return
     */
    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists())
            return -1;
        return mFile.length();
    }

    public static void createLogDir() {
        File file;
        boolean mkOk;

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            file = new File(LOG_PATH_SDCARD_DIR + File.separator + "AudioTalk");
            if (!file.isDirectory()) {
                mkOk = file.mkdirs();//TODO
                if (!mkOk) {
                    return;
                }
            }
        }
    }
}
