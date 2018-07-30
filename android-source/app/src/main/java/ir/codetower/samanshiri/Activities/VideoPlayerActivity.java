package ir.codetower.samanshiri.Activities;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private Timer timer;
    private TextView videoCurrentDurationTextView;
    private SeekBar seekBar;

    private RelativeLayout.LayoutParams portraitLayoutParams;
    private RelativeLayout.LayoutParams landscapeLayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        setupLayoutParams();
        setupVideo();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setupVideo() {
        videoView=(VideoView)findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("https://as9.cdn.asset.aparat.com/aparat-video/0375e115b798af49cc314f2fb2c105e38512596-360p__43674.apt?direct=1"));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                setupVideoControlersViews();
            }
        });
    }

    private void setupVideoControlersViews() {
        final ImageView playButton=(ImageView)findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()){
                    videoView.pause();
                    Picasso.with(App.context).load(R.drawable.ic_play).into(playButton);
                }else {
                    videoView.start();
                    Picasso.with(App.context).load(R.drawable.ic_pause).into(playButton);
                }
            }
        });


        TextView videoDurationTextView=(TextView)findViewById(R.id.text_video_duration);
        videoDurationTextView.setText(formatDuration(videoView.getDuration()));

        videoCurrentDurationTextView=(TextView)findViewById(R.id.text_video_current_duration);
        videoCurrentDurationTextView.setText(formatDuration(0));

        seekBar=(SeekBar)findViewById(R.id.seek_bar);
        seekBar.setMax(videoView.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       videoCurrentDurationTextView.setText(formatDuration(videoView.getCurrentPosition()));
                        seekBar.setProgress(videoView.getCurrentPosition());
                        seekBar.setSecondaryProgress((videoView.getBufferPercentage()*videoView.getDuration())/100);
                    }
                });
            }
        },0,1000);
    }

    private String formatDuration(long duration) {
        int seconds = (int) (duration / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(Locale.ENGLISH, "%02d", seconds);
    }

    @Override
    protected void onDestroy() {
        if (timer!=null){
            timer.purge();
            timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    private void setupLayoutParams(){
        View toolbar=findViewById(R.id.toolbar);
        View mediaController=findViewById(R.id.layout_media_controller);

        landscapeLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        portraitLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    }
}
