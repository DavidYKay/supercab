package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;


public class LaunchActivity extends RoboActivity {

  private static final String ANDROID_GCM_ID  = "1070528085113";
  private static final String ANDROID_GCM_KEY = "AIzaSyBe6BH85_wlDbBC3Rsv-SsoQdToFEEQhrI";
  private static final String TAG = "LaunchActivity";
  
    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    GCMRegistrar.checkDevice(this);
    GCMRegistrar.checkManifest(this);
    final String regId = GCMRegistrar.getRegistrationId(this);
    if (regId.equals("")) {
      //GCMRegistrar.register(this, SENDER_ID);
      GCMRegistrar.register(this, ANDROID_GCM_ID);
    } else {
      Log.v(TAG, "Already registered");
    }
  }
  
}
