package com.Online.popradio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    ImageView play;
    SeekBar seekBarVolume;
    MediaPlayer mediaPlayer;
    TextView PopOS;

    TextView song_n;
    private final static String url = "http://188.165.192.5:9231/stream";

    boolean prepared = false;
    boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final KeyEvent event =  intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                assert event != null;
                if (event.getAction() != KeyEvent.ACTION_DOWN) return;

                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        // stop music
                        break;
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        // pause music
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        // next track
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        // previous track
                        break;
                }
            }
        };
        PopOS =  findViewById(R.id.tpp);
        PopOS.setText("Loading...");


        imageView =  findViewById(R.id.Menuu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Info();
            }
        });
        play = findViewById(R.id.playme);
        play.setEnabled(false);
        //Problem Solved
        //Creating new MediaPlayer object every time and releasing it after completion
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        new PlayerTask().execute(url);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started) {
                    started = false;
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);


                } else {
                    started = true;
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);



                }
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                mediaPlayer.prepareAsync();
            }
        });

        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }



    private class PlayerTask extends AsyncTask <String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                prepared = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            play.setEnabled(true);
            play.setImageResource(R.drawable.play);
            Context context = getApplicationContext();
            CharSequence text = "Done!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            PopOS = findViewById(R.id.tpp);
            PopOS.setText("The Power Of The Philippines");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            // Argument equals true to notify the system that the activity
            // wishes to be visible behind other translucent activities
            if (!requestVisibleBehind(true)) {
                // App-specific method to stop playback and release resources
                // because call to requestVisibleBehind(true) failed
                stopPlayback();
            }
        } else {
            // Argument equals false because the activity is not playing
            requestVisibleBehind(false);
        }

    }

    private void stopPlayback() {

    }

    @Override
    public void onVisibleBehindCanceled() {
        // App-specific method to stop playback and release resources
        stopPlayback();
        super.onVisibleBehindCanceled();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared) {
            super.onDestroy();
            mediaPlayer.release();


        }
    }
    public  void  Info() {
        Intent intent = new Intent(this, Info.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        this.startActivity(intent);
    }
}
