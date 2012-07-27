package co.gargoyle.supercab.android.utilities;

import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

public class BroadcastUtils {

  public static final String ACTION_UPLOAD_DATA = "co.gargoyle.supercab.intent.action.uploadData";
  private static final String BROADCAST_NEW_DATA_RECEIVED = "co.gargoyle.supercab.intent.action.newDataReceived";
 
  private Context mContext;

  @Inject
  public BroadcastUtils(Context context) {
    mContext = context;
  }
  
  public void broadcastNewDataReceived() {
    Intent bindIntent = new Intent(BROADCAST_NEW_DATA_RECEIVED);
    mContext.sendBroadcast(bindIntent);
  }
  
  public void broadcastUploadData() {
    Intent bindIntent = new Intent(ACTION_UPLOAD_DATA);
    mContext.startService(bindIntent);
  }

}
