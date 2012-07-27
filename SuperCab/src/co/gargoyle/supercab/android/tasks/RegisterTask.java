package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.os.AsyncTask;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.network.UserRepresentation;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;
import co.gargoyle.supercab.android.utilities.CommonUtils;
import co.gargoyle.supercab.android.utilities.ServerUtils;

import com.google.common.base.Optional;

public class RegisterTask extends AsyncTask<UserModel, Integer, Optional<UserModel>> {
  
  @SuppressWarnings("unused")
  private static final String TAG = "PostUserTask";
  
  protected PostUserListener mListener;
  protected Exception mException;

  public RegisterTask(PostUserListener listener) {
    mListener = listener;
  }
  
  @Override
  protected Optional<UserModel> doInBackground(UserModel... credentialsList) {
    UserModel userModel = credentialsList[0];

    URI uri = getURI();

    ClientResource userResource = new ClientResource(uri);

    Optional<Representation> optional = ServerUtils.convertToJsonRepresentation(userModel);
    if (optional.isPresent()) {
      Representation userRepresentation = optional.get();
      try {
        Representation rep = userResource.post(userRepresentation);
        if (userResource.getStatus().isSuccess()) {
          try {
            UserRepresentation user = new UserRepresentation(rep);
//            String repText = rep.getText();
//            Log.d(TAG, "response: " + repText);
            return user.getUser();
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
    }

    return Optional.absent();
  }
  
  @Override
  protected void onPostExecute(Optional<UserModel> success) {
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
      String serverUrl = CommonUtils.SERVER_URL + "/register";
      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }
  
}
