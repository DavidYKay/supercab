/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.gargoyle.supercab.android.utilities;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtils {

  /**
   * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
   */
  //    static final String SERVER_URL = null;
  //public static final String SERVER_URL = "http://192.168.0.107:8000/api";
  //public static final String SERVER_URL = "http://192.168.0.107:8080/push";
  public static final String SERVER_URL = "http://192.168.0.104:8080";

  //public static final String SERVER_URL = "http://192.168.0.100:8080";
  
  //public static final String SERVER_URL = "http://supercab.herokuapp.com";

  //public static final String SERVER_URL = "http://requestb.in/pijt1epi";
  
  //public static final String SERVER_URL = "http://192.168.43.185:8080";

  // iHub
  //public static final String SERVER_URL = "http://192.168.1.207:8080";

  /**
   * Google API project id registered to use GCM.
   */
  public static final String SENDER_ID = "1070528085113";

  /**
   * Tag used on log messages.
   */
  static final String TAG = "GCMDemo";

  /**
   * Intent used to display a message in the screen.
   */
  public static final String DISPLAY_MESSAGE_ACTION =
      "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

  /**
   * Intent's extra that contains the message to be displayed.
   */
  public static final String EXTRA_MESSAGE = "message";

  /**
   * Notifies UI to display a message.
   * <p>
   * This method is defined in the common helper because it's used both by
   * the UI and the background service.
   *
   * @param context application's context.
   * @param message message to be displayed.
   */
  public static void displayMessage(Context context, String message) {
    Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
    intent.putExtra(EXTRA_MESSAGE, message);
    context.sendBroadcast(intent);
  }
}
