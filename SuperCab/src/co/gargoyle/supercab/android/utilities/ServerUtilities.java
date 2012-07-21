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

import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import android.content.Context;
import android.util.Log;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.model.Fare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.GCMRegistrar;
import com.google.common.base.Optional;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {
  

  private static final String TAG = "ServerUtilities";
  private static final int MAX_ATTEMPTS = 5;
  private static final int BACKOFF_MILLI_SECONDS = 2000;
  private static final Random random = new Random();

  /**
   * Register this account/device pair within the server.
   * 
   * @return whether the registration succeeded or not.
   */
  public static boolean register(final Context context, final String regId) {
    Log.i(TAG, "registering device (regId = " + regId + ")");
    //String serverUrl = CommonUtilities.SERVER_URL + "/device/";
    String serverUrl = CommonUtilities.SERVER_URL + "/push/register";
    Map<String, String> params = new HashMap<String, String>();
    params.put("push_id", regId);
    long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
    // Once GCM returns a registration id, we need to register it in the
    // demo server. As the server might be down, we will retry it a couple
    // times.
    for (int i = 1; i <= MAX_ATTEMPTS; i++) {
      Log.d(TAG, "Attempt #" + i + " to register");
      try {
        CommonUtilities.displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
        //post(serverUrl, params);
        postRestlet(serverUrl, params);
        GCMRegistrar.setRegisteredOnServer(context, true);
        String message = context.getString(R.string.server_registered);
        CommonUtilities.displayMessage(context, message);
        return true;
      } catch (IOException e) {
        // Here we are simplifying and retrying on any error; in a real
        // application, it should retry only on unrecoverable errors
        // (like HTTP error code 503).
        Log.e(TAG, "Failed to register on attempt " + i, e);
        if (i == MAX_ATTEMPTS) {
          break;
        }
        try {
          Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
          Thread.sleep(backoff);
        } catch (InterruptedException e1) {
          // Activity finished before we complete - exit.
          Log.d(TAG, "Thread interrupted: abort remaining retries!");
          Thread.currentThread().interrupt();
          return false;
        }
        // increase backoff exponentially
        backoff *= 2;
      }
    }
    String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
    CommonUtilities.displayMessage(context, message);
    return false;
  }

  /**
   * Unregister this account/device pair within the server.
   */
  public static void unregister(final Context context, final String regId) {
    Log.i(TAG, "unregistering device (regId = " + regId + ")");
    String serverUrl = CommonUtilities.SERVER_URL + "/push/unregister";
    //String serverUrl = CommonUtilities.SERVER_URL + "/unregister";
    //String serverUrl = CommonUtilities.SERVER_URL + "/device/";
    Map<String, String> params = new HashMap<String, String>();
    params.put("push_id", regId);
    try {
      delete(serverUrl, params);
      GCMRegistrar.setRegisteredOnServer(context, false);
      String message = context.getString(R.string.server_unregistered);
      CommonUtilities.displayMessage(context, message);
    } catch (IOException e) {
      // At this point the device is unregistered from GCM, but still
      // registered in the server.
      // We could try to unregister again, but it is not necessary:
      // if the server tries to send a message to the device, it will get
      // a "NotRegistered" error message and should unregister the device.
      String message = context.getString(R.string.server_unregister_error, e.getMessage());
      CommonUtilities.displayMessage(context, message);
    }
  }
  
  public static boolean postRestlet(String endpoint, Map<String, String> params) throws IOException {
    //URI uri = getDeviceURI();
    URI uri = makeURI(endpoint);
    ClientResource deviceResource = new ClientResource(uri);

    deviceResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "driver", "driver");

    Representation representation = convertMapToJsonRepresentation(params);

    //Log.d(TAG, "JSON text: " + jsonText);

    deviceResource.post(representation);
    if (deviceResource.getStatus().isSuccess()) {
      String responseText = representation.getText();
      Log.d(TAG, "response: " + responseText);
      return true;
    } else {
      Log.e(TAG, "error!: " + deviceResource.getStatus());
      // error!
      return false;
    }
  }

  /**
   * Issue a POST request to the server.
   * 
   * @param endpoint
   *          POST address.
   * @param params
   *          request parameters.
   * 
   * @throws IOException
   *           propagated from POST.
   */
  private static void post(String endpoint, Map<String, String> params) throws IOException {

    setupAuthentication();

    URL url;
    try {
      url = new URL(endpoint);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("invalid url: " + endpoint);
    }
    StringBuilder bodyBuilder = new StringBuilder();
    Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
    // constructs the POST body using the parameters
    while (iterator.hasNext()) {
      Entry<String, String> param = iterator.next();
      bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
      if (iterator.hasNext()) {
        bodyBuilder.append('&');
      }
    }
    String body = bodyBuilder.toString();
    Log.v(TAG, "Posting '" + body + "' to " + url);
    byte[] bytes = body.getBytes();
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      conn.setFixedLengthStreamingMode(bytes.length);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
      // post the request
      OutputStream out = conn.getOutputStream();
      out.write(bytes);
      out.close();
      // handle the response
      int status = conn.getResponseCode();
      if (status != 200) {
        throw new IOException("Post failed with error code " + status);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }
  
  /**
   * Issue a DELETE request to the server.
   * 
   * @param endpoint
   *          DELETE address.
   * @param params
   *          request parameters.
   * 
   * @throws IOException
   *           propagated from DELETE.
   */
  private static void delete(String endpoint, Map<String, String> params) throws IOException {
    URL url;
    try {
      url = new URL(endpoint);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("invalid url: " + endpoint);
    }
    StringBuilder bodyBuilder = new StringBuilder();
    Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
    // constructs the POST body using the parameters
    while (iterator.hasNext()) {
      Entry<String, String> param = iterator.next();
      bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
      if (iterator.hasNext()) {
        bodyBuilder.append('&');
      }
    }
    String body = bodyBuilder.toString();
    Log.v(TAG, "Posting '" + body + "' to " + url);
    byte[] bytes = body.getBytes();
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      conn.setFixedLengthStreamingMode(bytes.length);
      conn.setRequestMethod("DELETE");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
      // post the request
      OutputStream out = conn.getOutputStream();
      out.write(bytes);
      out.close();
      // handle the response
      int status = conn.getResponseCode();
      if (status != 200) {
        throw new IOException("Post failed with error code " + status);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  private static boolean hasSetupAuth = false;
  private static void setupAuthentication() {
    if (!hasSetupAuth) {
      Authenticator.setDefault(new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          String username = "driver";
          String password = "driver";
          return new PasswordAuthentication(username, password.toCharArray());
        };
      });
      hasSetupAuth = true;
    }
  }
  
  private static URI makeURI(String endpoint) {
    try {
      //URI uri = new URI(CommonUtilities.SERVER_URL + "/register");
      URI uri = new URI(endpoint);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

  private static Form convertMapToForm(Map<String, String> map) {
    Form form = new Form();
    for (Entry<String, String> entry : map.entrySet()) {
      Parameter parameter = new Parameter(entry.getKey(), entry.getValue());
      form.add(parameter);
    }

    return form;
  }

  private static Optional<Representation> convertMapToWebRepresentation(Map<String, String> map) {
    Form form = convertMapToForm(map);
    return Optional.of(form.getWebRepresentation());
  }
  
  private static Optional<String> convertMapToFormEncodedString(Map<String, String> map) {
    Form form = convertMapToForm(map);
    try {
      String encodedForm = form.encode();
      return Optional.of(encodedForm);
    } catch (IOException exception) {
      exception.printStackTrace();
      return Optional.absent();
    }
  }
  
  public static Representation convertFareToJsonRepresentation(Fare fare) throws IOException {
    JacksonRepresentation<Fare> jacksonRep = new JacksonRepresentation<Fare>(fare);

    jacksonRep.setObjectMapper(new ObjectMapper());

    String jsonText = jacksonRep.getText();

    Representation textRep = new StringRepresentation(jsonText);
    textRep.setMediaType(MediaType.APPLICATION_JSON);
    return textRep;
  }
    
  public static Representation convertMapToJsonRepresentation(Map<String, String> map) throws IOException {
    JacksonRepresentation<Map<String,String>> jacksonRep = new JacksonRepresentation<Map<String,String>>(map);

    jacksonRep.setObjectMapper(new ObjectMapper());

    String jsonText = jacksonRep.getText();
    Log.v(TAG, "New JSON Text: " + jsonText);

    Representation textRep = new StringRepresentation(jsonText);
    textRep.setMediaType(MediaType.APPLICATION_JSON);
    return textRep;
  }
}
