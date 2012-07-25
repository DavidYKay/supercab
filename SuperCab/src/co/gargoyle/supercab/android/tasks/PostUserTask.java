package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.common.base.Optional;

import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;
import co.gargoyle.supercab.android.utilities.CommonUtilities;
import co.gargoyle.supercab.android.utilities.ServerUtilities;

public class PostUserTask extends AsyncTask<UserModel, Integer, Boolean> {
  
  private static final String TAG = "PostUserTask";
  
  protected PostUserListener mListener;
  protected ResourceException mException;

  public PostUserTask(PostUserListener listener) {
    mListener = listener;
  }
  
  @Override
  protected Boolean doInBackground(UserModel... credentialsList) {
    UserModel userModel = credentialsList[0];

    URI uri = getURI();

    ClientResource userResource = new ClientResource(uri);
    userResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, 
                                     userModel.username, 
                                     userModel.password);

    Optional<Representation> optional = ServerUtilities.convertToJsonRepresentation(userModel);
    if (optional.isPresent()) {
      Representation userRepresentation = optional.get();
      try {
        Representation rep = userResource.post(userRepresentation);
        if (userResource.getStatus().isSuccess()) {
          try {
            Log.d(TAG, "response: " + rep.getText());
            return true;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } catch (ResourceException e) {
        mException = e;
      }
    }

    return false;
  }
  
  @Override
  protected void onPostExecute(Boolean success) {
    if (mException != null) {
      mListener.handleError(mException);
    } else {
      mListener.completed(success);
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
  
}
