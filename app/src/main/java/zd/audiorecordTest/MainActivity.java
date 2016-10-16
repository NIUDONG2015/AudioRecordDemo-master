package zd.audiorecordTest;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import zd.audiorecordTest.audio.AudioFileFunc;
import zd.audiorecordTest.audio.AudioRecordFunc;

public class MainActivity extends AppCompatActivity {

    private Button btn_record, btn_stop, btn_play;
    private TextView mTextView;
    private ThreadAudioTrack threadAudioTrack;
    private AudioTrack audioTrack;
    private boolean blnPlay = false;// 设置正在录制的状态
    private int playBufSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        AudioFileFunc.createLogDir();//创建文件夹
    }


    private void initView() {
        mTextView = (TextView) findViewById(R.id.textView);
        btn_record = (Button) findViewById(R.id.btn_record_wav);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_play = (Button) findViewById(R.id.btn_play_wav);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record();
                mTextView.setText("正在录制");
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                mTextView.setText("录制完成");
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                play(AudioFileFunc.getWavFilePath());
            }
        });
    }

    private void record() {
        AudioRecordFunc.getInstance().startRecordAndFile();
    }

    private void stop() {
        AudioRecordFunc.getInstance().stopRecordAndFile();
    }

    private void play(String path) {
//   AudioRecordFunc.getInstance().playRecord(path);
        threadAudioTrack = new ThreadAudioTrack();
        threadAudioTrack.init();
        threadAudioTrack.start();
    }

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
