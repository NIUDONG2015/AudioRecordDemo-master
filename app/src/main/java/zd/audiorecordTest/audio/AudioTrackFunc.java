package zd.audiorecordTest.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/10/16.
 */

public  class AudioTrackFunc {
    private AudioTrack audioTrack;
    private boolean blnPlay = false;// 设置正在录制的状态
    private int playBufSize = 0;
    private static AudioTrackFunc mInstance;

/*
    private AudioTrackFunc() {

    }

    public synchronized static AudioTrackFunc getInstance() {
        if (mInstance == null)
            mInstance = new AudioTrackFunc();
        return mInstance;
    }
*/

/*  停止播放
    threadAudioTrack.free();
    threadAudioTrack = null;*/

/*  开始播放
    threadAudioTrack = new ThreadAudioTrack();
    threadAudioTrack.init();
    threadAudioTrack.start();*/



    /**
     * 播放录音线程
     */
    class ThreadAudioTrack extends Thread {
        byte[] bs;

        File file;
        FileInputStream fis;

        /**
         * 初始化AudioTrack
         */
        public void init() {
           /* file = new File("/sdcard/" + AUDIO_RECORDER_FOLDER + "/",
                    AUDIO_RECORDER_FILE); */

            file = new File(AudioFileFunc.getWavFilePath());
            try {
                file.createNewFile();//这里会有问题么？？
                fis = new FileInputStream(file);//输入流

                blnPlay = true;

                playBufSize = AudioTrack.getMinBufferSize(AudioFileFunc.AUDIO_SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFileFunc.AUDIO_FORMAT);

                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AudioFileFunc.AUDIO_SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFileFunc.AUDIO_FORMAT, playBufSize, AudioTrack.MODE_STREAM);

                bs = new byte[playBufSize];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 停止播放    释放AudioTrack
         */
        public void free() {
            blnPlay = false;
        }

        @Override
        public void run() {
            audioTrack.play();
            while (blnPlay) {
                try {

                    int line = fis.read(bs, 0, AudioRecordFunc.getInstance().bufferSizeInBytes);

                    if (line == -1) {//没有内容啦
                        blnPlay = false;
//                        handler.sendMessage(new Message());//发送空消息体
                        return;
                    }

                    byte[] tmpBuf = new byte[line];
                    System.arraycopy(bs, 0, tmpBuf, 0, line);

//					fis.read(bs);
//					tmpBuf = bs.clone();

                    audioTrack.write(tmpBuf, 0, tmpBuf.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            audioTrack.stop();
            audioTrack = null;
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

