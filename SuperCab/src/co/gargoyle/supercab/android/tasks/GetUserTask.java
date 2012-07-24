package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.UserCredentials;
import co.gargoyle.supercab.android.model.UserProfile;
import co.gargoyle.supercab.android.tasks.listeners.GetUserListener;
import co.gargoyle.supercab.android.utilities.CommonUtilities;

import com.google.common.base.Optional;

public class GetUserTask extends AsyncTask<UserCredentials, Integer, Optional<UserProfile>> {
  
  private static final String TAG = "GetUserTask";

  protected GetUserListener mListener;
  protected ResourceException mException;

   public GetUserTask(GetUserListener listener) {
     mListener = listener;
   }

  @Override
  protected Optional<UserProfile> doInBackground(UserCredentials... credentialsList) {
    //User fare = fares[0];
    UserCredentials creds = credentialsList[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);

    fareProfile.setChallengeResponse(ChallengeScheme.HTTP_BASIC, 
                                     creds.username, 
                                     creds.password);

    try {
      Representation rep = fareProfile.get();
      if (fareProfile.getStatus().isSuccess()) {
        try {
          Log.d(TAG, "response: " + rep.getText());
//          return true;
          UserProfile user = representationToUser(rep);
          return Optional.of(user);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (ResourceException e) {
      mException = e;
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<UserProfile> user) {
    if (mException != null) {
      mListener.handleError(mException);
    } else {
      mListener.completed(user);
    }
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  protected URI getURI() {
    try {
      String serverUrl = CommonUtilities.SERVER_URL + "/api/v1/user/";
      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

  private UserProfile representationToUser(Representation rep) {
    // TODO Auto-generated method stub
    return null;
  }

}
