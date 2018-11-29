package fr.wildcodeschool.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class MediaButtonReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context ctx, Intent intent)
  {
    LocalBroadcastManager.getInstance(MainActivity.getAppContext()).sendBroadcast(intent);
  }
}
