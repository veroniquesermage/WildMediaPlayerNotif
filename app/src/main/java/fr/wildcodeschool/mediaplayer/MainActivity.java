package fr.wildcodeschool.mediaplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity  {
  // Audio player
  //private WildPlayer mPlayer = null;
  // Progress bar
//  private SeekBar mSeekbar = null;
//  // Seekbar update delay
//  private static final int SEEKBAR_DELAY = 1000;
//  // Thread used to update the seekbar position
//  private final Handler mSeekBarHandler = new Handler();
//  private Runnable mSeekBarThread;

  // Application Context is static in order to access it everywhere.
  private static Context appContext;

//  private static final String ACTION_PLAY = "PLAY";
//  private static final String ACTION_PAUSE = "PAUSE";
//  private static final String ACTION_RESET = "RESET";
  private static final String CHANNEL_ID = "audioPlayer";
  private static final int NOTIFICATION_ID = 1;
  private BroadcastReceiver mBroadcastReceiverPlayer = null;
  private NotificationCompat.Builder mNotifBuilder = null;

  private MediaPlayerService mMediaPlayerService;

  private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mMediaPlayerService = ((MediaPlayerService.LocalBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mMediaPlayerService = null;
    }
  };

  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialization of the application context
    MainActivity.appContext = getApplicationContext();

//    // Initialization of the wild audio player
//    mPlayer = new WildPlayer(this);
//    mPlayer.init(R.string.song, new WildOnPlayerListener() {
//      @Override
//      public void onPrepared(MediaPlayer mp) {
//        mSeekbar.setMax(mp.getDuration());
//      }
//
//      @Override
//      public void onCompletion(MediaPlayer mp) {
//        mSeekBarHandler.removeCallbacks(mSeekBarThread);
//        mSeekbar.setProgress(0);
//      }
//    });

//    // Initialization of the seekbar
//    mSeekbar = findViewById(R.id.seekBar);
//    mSeekbar.setOnSeekBarChangeListener(this);
//
//    // Thread used to update the seekbar position according to the audio player
//    mSeekBarThread = new Runnable() {
//      @Override
//      public void run() {
//        // Widget should only be manipulated in UI thread
//        mSeekbar.post(() -> mSeekbar.setProgress(mPlayer.getCurrentPosition()));
//        // Launch a new request
//        mSeekBarHandler.postDelayed(this, SEEKBAR_DELAY);
//      }
//    };


    // Get notification service Instance
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//    // Create PLAY intent
//    Intent playIntent = new Intent(ACTION_PLAY);
//    playIntent.setClass(this, MediaButtonReceiver.class);
//    PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, 0);
//
//    // Create PAUSE intent
//    Intent pauseIntent = new Intent(ACTION_PAUSE);
//    pauseIntent.setClass(this, MediaButtonReceiver.class);
//    PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 1, pauseIntent, 0);
//
//    // Create RESET intent
//    Intent stopIntent = new Intent(ACTION_RESET);
//    stopIntent.setClass(this, MediaButtonReceiver.class);
//    PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 2, stopIntent, 0);

    // Populate notification
    mNotifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
      // Show controls on lock screen even when user hides sensitive content.
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setSmallIcon(R.drawable.ic_stat_music_note)
      .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.queen))
      // Add media control buttons that invoke intents in your media service
//      .addAction(R.drawable.ic_stat_play_arrow, "Play", playPendingIntent) // #0
//      .addAction(R.drawable.ic_stat_pause, "Pause", pausePendingIntent)    // #1
//      .addAction(R.drawable.ic_stat_stop, "Reset", stopPendingIntent)      // #2
      .setContentTitle("Bohemian Rhapsody")
      .setContentText("My Awesome Band");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Get media session compat Instance
      MediaSessionCompat lMediaSessionCompat = initMediaSession();

//      lMediaSessionCompat.setMediaButtonReceiver(playPendingIntent);
//      lMediaSessionCompat.setMediaButtonReceiver(pausePendingIntent);
//      lMediaSessionCompat.setMediaButtonReceiver(stopPendingIntent);

      // Apply the media style template
      mNotifBuilder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
        .setShowActionsInCompactView(0 /* #0: play button  */)
        .setShowActionsInCompactView(1 /* #1: pause button */)
        .setShowActionsInCompactView(2 /* #2: stop button  */)
        .setMediaSession(lMediaSessionCompat.getSessionToken()));
    }

    // Build the notification
    notificationManager.notify(NOTIFICATION_ID, mNotifBuilder.build());

    //Code pour le service
    Intent serviceIntent = new Intent(this, MediaPlayerService.class);
    bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
    startService(serviceIntent);




  }

//  @Override
//  protected void onStart() {
//    super.onStart();
//
//    mBroadcastReceiverPlayer = new BroadcastReceiver() {
//      @Override
//      public void onReceive(Context context, Intent intent) {
//        if (null != intent) {
//          switch (intent.getAction()) {
//            case ACTION_PLAY:
//              playMedia(null);
//              break;
//            case ACTION_PAUSE:
//              pauseMedia(null);
//              break;
//            case ACTION_RESET:
//              resetMedia(null);
//              break;
//          }
//        }
//      }
//    };

//    LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);
//    mgr.registerReceiver(mBroadcastReceiverPlayer, new IntentFilter(ACTION_PLAY));
//    mgr.registerReceiver(mBroadcastReceiverPlayer, new IntentFilter(ACTION_PAUSE));
//    mgr.registerReceiver(mBroadcastReceiverPlayer, new IntentFilter(ACTION_RESET));
//  }

  @Override
  protected void onPause() {
    super.onPause();

    if (null != mNotifBuilder) {
      // Get notification service Instance
      NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      //notificationManager.notify(NOTIFICATION_ID, mNotifBuilder.build());

      //Code du service mediaPlayer
      unregisterReceiver(mBroadcastReceiver);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Get notification service Instance
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    //notificationManager.cancel(NOTIFICATION_ID);

    //Code du service mediaPlayer
    registerReceiver(mBroadcastReceiver, new IntentFilter(mMediaPlayerService.BROADCAST_ACTION));
  }

  @Override
  protected void onStop() {
    super.onStop();
    LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);
    mgr.unregisterReceiver(mBroadcastReceiverPlayer);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbindService(mServiceConnection);
  }

  /**
   *
   * @return
   */
  private MediaSessionCompat initMediaSession() {
    ComponentName mediaButtonReceiver = new ComponentName(
      getApplicationContext(), MediaButtonReceiver.class);

    MediaSessionCompat lMediaSessionCompat = new MediaSessionCompat(
      getApplicationContext(),"Tag", mediaButtonReceiver,null);

    lMediaSessionCompat.setFlags(
      MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
      MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS );

    return lMediaSessionCompat;
  }

  /**
   * OnSeekBarChangeListener interface method implementation
   * @param seekBar Widget related to the event
   * @param progress Current position on the seekbar
   * @param fromUser Define if it is a user action or a programmatic seekTo
   */
//  @Override
//  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//      if (fromUser) {
//        mPlayer.seekTo(progress);
//      }
//  }
//
//  /**
//   * OnSeekBarChangeListener interface method implementation
//   * @param seekBar Widget related to the event
//   */
//  @Override
//  public void onStartTrackingTouch(SeekBar seekBar) {
//    Log.e("Activity", "onStartTrackingTouch");
//    // Stop seekBarUpdate here
//    mSeekBarHandler.removeCallbacks(mSeekBarThread);
//  }

  /**
   * OnSeekBarChangeListener interface method implementation
   * @param seekBar Widget related to the event
   */
//  @Override
//  public void onStopTrackingTouch(SeekBar seekBar) {
//    Log.e("Activity", "onStopTrackingTouch");
//    // Restart seekBarUpdate here
//    if (null != mPlayer && mPlayer.isPlaying()) {
//      mSeekBarHandler.postDelayed(mSeekBarThread, SEEKBAR_DELAY);
//    }
//  }
//
//  /**
//   * On play button click
//   * Launch the playback of the media
//   */
//  public void playMedia(View v) {
//    if (null != mPlayer && mPlayer.play()) {
//      mSeekBarHandler.postDelayed(mSeekBarThread, SEEKBAR_DELAY);
//    }
//  }
//
//  /**
//   * On pause button click
//   * Pause the playback of the media
//   */
//  public void pauseMedia(View v) {
//    if (null != mPlayer && mPlayer.pause()) {
//      mSeekBarHandler.removeCallbacks(mSeekBarThread);
//    }
//  }
//
//  /**
//   * On reset button click
//   * Stop the playback of the media
//   */
//  public void resetMedia(View v) {
//    if (null != mPlayer && mPlayer.reset()) {
//      mSeekbar.setProgress(0);
//    }
//  }

  /**
   * Application context accessor
   * https://possiblemobile.com/2013/06/context/
   * @return The application context
   */
  public static Context getAppContext() {
    return appContext;
  }

  private static IntentFilter makeIntentFilter(){
    final IntentFilter intentFilter= new IntentFilter();
    intentFilter.addAction(MediaPlayerService.BROADCAST_ACTION);
    intentFilter.addAction(MediaPlayerService.ACTION_PLAY);
    intentFilter.addAction(MediaPlayerService.ACTION_PAUSE);
    intentFilter.addAction(MediaPlayerService.ACTION_RESET);

    return intentFilter;
  }
}
