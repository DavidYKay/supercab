package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.ChallengeScheme;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.UserCredentials;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.network.UserRepresentation;
import co.gargoyle.supercab.android.tasks.listeners.GetUserListener;
import co.gargoyle.supercab.android.utilities.CommonUtilities;

import com.google.common.base.Optional;

public class GetUserTask extends AsyncTask<UserCredentials, Integer, Optional<UserModel>> {

  private static final String TAG = "GetUserTask";

  protected GetUserListener mListener;
  protected Exception mException;

  public GetUserTask(GetUserListener listener) {
    mListener = listener;
  }

  @Override
  protected Optional<UserModel> doInBackground(UserCredentials... credentialsList) {
    //User fare = fares[0];
    UserCredentials creds = credentialsList[0];

    URI uri = getURI();

    ClientResource clientResource = new ClientResource(uri);

    clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, 
        creds.username, 
        creds.password);
    
//    UserResource fareProfile = clientResource.wrap(UserResource.class);


    try {
      Representation rep = clientResource.get();
      if (clientResource.getStatus().isSuccess()) {
        try {
          Log.d(TAG, "response: " + rep.getText());
          UserRepresentation userRep = new UserRepresentation(rep);
          UserModel user = userRep.getUser();
          if (user == null) {
            return Optional.absent();
          } else {
            return Optional.of(user); 
          }
        } catch (IOException e) {
          e.printStackTrace();
          mException = e;
        } catch (Exception e) {
          e.printStackTrace();
          mException = e;
        }
      }
    } catch (ResourceException e) {
      mException = e;
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<UserModel> user) {
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

  private UserModel representationToUser(Representation rep) {
    JacksonRepresentation<UserModel> jRep = new JacksonRepresentation<UserModel>(rep, UserModel.class);
    return jRep.getObject();
  }

}
