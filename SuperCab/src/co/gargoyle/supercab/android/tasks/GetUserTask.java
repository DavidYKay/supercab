package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.UserCredentials;
import co.gargoyle.supercab.android.utilities.CommonUtilities;

public class GetUserTask extends AsyncTask<UserCredentials, Integer, Boolean> {
  
  private static final String TAG = "GetUserTask";

  private GetUserListener mListener;

   public GetUserTask(GetUserListener listener) {
     mListener = listener;
   }

  @Override
  protected Boolean doInBackground(UserCredentials... credentialsList) {
    //User fare = fares[0];
    UserCredentials creds = credentialsList[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);

    fareProfile.setChallengeResponse(ChallengeScheme.HTTP_BASIC, 
                                     creds.username, 
                                     creds.password);

    Representation rep = fareProfile.get();
    if (fareProfile.getStatus().isSuccess()) {
      try {
        Log.d(TAG, "response: " + rep.getText());
        return true;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return false;
  }

  @Override
  protected void onPostExecute(Boolean success) {
     mListener.completed(success);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  private URI getURI() {
    try {
      String serverUrl = CommonUtilities.SERVER_URL + "/api/v1/user/";
      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

}
