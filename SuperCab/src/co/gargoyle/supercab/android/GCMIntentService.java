package co.gargoyle.supercab.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import co.gargoyle.supercab.android.activities.LaunchActivity;
import co.gargoyle.supercab.android.utilities.CommonUtils;
import co.gargoyle.supercab.android.utilities.ServerUtils;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
  private static final String TAG = "GCMIntentService";
  
  public GCMIntentService() {
    super(CommonUtils.SENDER_ID);
  }

  @Override
  public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Called after a registration intent is received, passes the registration ID
   * assigned by GCM to that device/application pair as parameter. Typically,
   * you should send the regid to your server so it can use it to send messages
   * to this device.
   */
  @Override
  protected void onRegistered(Context context, String registrationId) {
    Log.d(TAG, "onRegistered(): " + registrationId);
        
    CommonUtils.displayMessage(context, getString(R.string.gcm_registered));
    ServerUtils.register(context, registrationId);
  }

  /**
   * Called after the device has been unregistered from GCM. Typically, you
   * should send the regid to the server so it unregisters the device.
   */
  @Override
  protected void onUnregistered(Context context, String registrationId) {
    Log.d(TAG, "onUnregistered(): " + registrationId);

    CommonUtils.displayMessage(context, getString(R.string.gcm_unregistered));
    if (GCMRegistrar.isRegisteredOnServer(context)) {
      ServerUtils.unregister(context, registrationId);
    } else {
      // This callback results from the call to unregister made on
      // ServerUtilities when the registration to the server failed.
      Log.i(TAG, "Ignoring unregister callback");
    }
  }

  /**
   * Called when your server sends a message to GCM, and GCM delivers it to the
   * device. If the message has a payload, its contents are available as extras
   * in the intent.
   */
  @Override
  protected void onMessage(Context context, Intent intent) {
    Log.d(TAG, "onMessage(): " + intent.toString());
        
    String message = getString(R.string.gcm_message);
    CommonUtils.displayMessage(context, message);
    // notifies user
    generateNotification(context, message);
  }
    
  @Override
  protected void onDeletedMessages(Context context, int total) {
    Log.i(TAG, "Received deleted messages notification");
    String message = getString(R.string.gcm_deleted, total);
    CommonUtils.displayMessage(context, message);
    // notifies user
    generateNotification(context, message);
  }

  /**
   * Called when the device tries to register or unregister, but GCM returned an
   * error. Typically, there is nothing to be done other than evaluating the
   * error (returned by errorId) and trying to fix the problem.
   */
  @Override
  protected void onError(Context context, String errorId) {
    Log.d(TAG, "onError(): " + errorId);
    CommonUtils.displayMessage(context, getString(R.string.gcm_error, errorId));
  }

  /**
   * Called when the device tries to register or unregister, but the GCM servers
   * are unavailable. The GCM library will retry the operation using exponential
   * backup, unless this method is overridden and returns false. This method is
   * optional and should be overridden only if you want to display the message
   * to the user or cancel the retry attempts.
   */
  @Override
  protected boolean onRecoverableError(Context context, String errorId) {
    Log.d(TAG, "onRecoverableError(): " + errorId);

    CommonUtils.displayMessage(context, getString(R.string.gcm_recoverable_error,
                                      errorId));
    return super.onRecoverableError(context, errorId);
  }
    
  /**
   * Issues a notification to inform the user that server has sent a message.
   */
  private static void generateNotification(Context context, String message) {
    int icon = R.drawable.ic_stat_gcm;
    long when = System.currentTimeMillis();
    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = new Notification(icon, message, when);
    String title = context.getString(R.string.app_name);
    Intent notificationIntent = new Intent(context, LaunchActivity.class);
    // set intent so it does not start a new activity
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent intent =
        PendingIntent.getActivity(context, 0, notificationIntent, 0);
    notification.setLatestEventInfo(context, title, message, intent);
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    notificationManager.notify(0, notification);
  }
}
