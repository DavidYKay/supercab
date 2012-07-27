package co.gargoyle.supercab.android.services;

import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class BackgroundUploadService extends IntentService {
  private static final String SERVICE_NAME = "BackgroundUploadService";

  //  private PhoneUtils mPhoneUtils;

  @SuppressWarnings("unused")
  private Context mContext;

  public BackgroundUploadService() {
    super(SERVICE_NAME);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    mContext = this;
    //    mPhoneUtils = new PhoneUtils(mContext);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent.getAction().equals(BroadcastUtils.ACTION_UPLOAD_DATA)) {

//      uploadSmsEvents();
    }
  }

}
