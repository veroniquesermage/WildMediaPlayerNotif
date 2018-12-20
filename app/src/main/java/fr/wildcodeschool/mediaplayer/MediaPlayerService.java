package fr.wildcodeschool.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.util.List;

public class MediaPlayerService extends MediaBrowserServiceCompat implements SeekBar.OnSeekBarChangeListener {

    public static final String BROADCAST_ACTION = "fr.wildcodeschool.mediaplayer.BROADCAST_ACTION";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_RESET = "RESET";
    public static final String MEDIAPLAYER_SERVICE_INTENT = "fr.wildcodeschool.mediaplayer.MEDIAPLAYER_SERVICE_INTENT";
    private final Binder binder = new LocalBinder();
    private WildPlayer mPlayer;

    //Déclaration de la seekbar
    private SeekBar mSeekbar = null;
    // Seekbar update delay
    private static final int SEEKBAR_DELAY = 1000;
    // Thread used to update the seekbar position
    private final Handler mSeekBarHandler = new Handler();
    private Runnable mSeekBarThread;




    private Intent broadcast = new Intent(MEDIAPLAYER_SERVICE_INTENT);

    public void broadcastUpdate (){
        sendBroadcast(broadcast);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initialization of the wild audio player
        mPlayer = new WildPlayer(this);
        mPlayer.init(R.string.song, new WildOnPlayerListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mSeekbar.setMax(mp.getDuration());
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                mSeekBarHandler.removeCallbacks(mSeekBarThread);
                mSeekbar.setProgress(0);
            }
        });


//        mBroadcastReceiverPlayer = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (null != intent) {
//                    switch (intent.getAction()) {
//                        case ACTION_PLAY:
//                            playMedia(null);
//                            break;
//                        case ACTION_PAUSE:
//                            pauseMedia(null);
//                            break;
//                        case ACTION_RESET:
//                            resetMedia(null);
//                            break;
//                    }
//                }
//            }
//        };
//
//    LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);
//    mgr.registerReceiver(mBroadcastReceiverPlayer, new IntentFilter(ACTION_PLAY));
//    mgr.registerReceiver(mBroadcastReceiverPlayer, new IntentFilter(ACTION_PAUSE));
//    mgr.registerReceiver(mBroadcastReceiverPlayer, new IntentFilter(ACTION_RESET));


        // Initialization of the seekbar
        mSeekbar = mSeekbar.findViewById(R.id.seekBar);
        mSeekbar.setOnSeekBarChangeListener(this);

        // Thread used to update the seekbar position according to the audio player
        mSeekBarThread = new Runnable() {
            @Override
            public void run() {
                // Widget should only be manipulated in UI thread
                mSeekbar.post(() -> mSeekbar.setProgress(mPlayer.getCurrentPosition()));
                // Launch a new request
                mSeekBarHandler.postDelayed(this, SEEKBAR_DELAY);
            }
        };

        return START_STICKY;
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPlayer.seekTo(progress);
        }
    }

    /**
     * OnSeekBarChangeListener interface method implementation
     * @param seekBar Widget related to the event
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("Activity", "onStartTrackingTouch");
        // Stop seekBarUpdate here
        mSeekBarHandler.removeCallbacks(mSeekBarThread);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.e("Activity", "onStopTrackingTouch");
        // Restart seekBarUpdate here
        if (null != mPlayer && mPlayer.isPlaying()) {
            mSeekBarHandler.postDelayed(mSeekBarThread, SEEKBAR_DELAY);
        }
    }

    /**
     * On play button click
     * Launch the playback of the media
     */
    public void playMedia(View v) {
        if (null != mPlayer && mPlayer.play()) {
            mSeekBarHandler.postDelayed(mSeekBarThread, SEEKBAR_DELAY);
        }
    }

    /**
     * On pause button click
     * Pause the playback of the media
     */
    public void pauseMedia(View v) {
        if (null != mPlayer && mPlayer.pause()) {
            mSeekBarHandler.removeCallbacks(mSeekBarThread);
        }
    }

    /**
     * On reset button click
     * Stop the playback of the media
     */
    public void resetMedia(View v) {
        if (null != mPlayer && mPlayer.reset()) {
            mSeekbar.setProgress(0);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String s, int i, @Nullable Bundle bundle) {
        broadcastUpdate();
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String s, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    public class LocalBinder extends Binder{
        MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
    }
}
