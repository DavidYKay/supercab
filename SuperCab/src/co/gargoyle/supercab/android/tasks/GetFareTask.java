package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.Fare;

public class GetFareTask extends AsyncTask<Fare, Integer, Boolean> {

  // private GetPhoneCallListener mListener;

  // public GetPhoneCallTask(GetPhoneCallListener listener) {
  // mListener = listener;
  // }

  private static final String TAG = "GetFareTask";

  @Override
  protected Boolean doInBackground(Fare... fares) {
    Fare fare = fares[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);
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
    // mListener.completed(success);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  private URI getURI() {
    try {
      URI uri = new URI("http://192.168.0.107/");
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

}
