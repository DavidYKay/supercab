package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.content.Context;
import android.os.AsyncTask;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.network.FareRepresentation;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;
import co.gargoyle.supercab.android.utilities.CommonUtilities;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;
import co.gargoyle.supercab.android.utilities.ServerUtilities;

import com.google.common.base.Optional;

public class PostFareTask extends AsyncTask<Fare, Integer, Optional<String>> {


  @SuppressWarnings("unused")
  private static final String TAG = "UploadFareTask";

  private PostFareListener mListener;
  private Exception mException;
  private Context mContext;

  private PreferenceUtils mPreferenceUtils;

  public PostFareTask(Context context, PostFareListener listener) {
    mListener = listener;
    mContext = context;
    mPreferenceUtils = new PreferenceUtils(mContext);
  }

  @Override
  protected Optional<String> doInBackground(Fare... fares) {
    Fare fare = fares[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);

    Optional<String> token = mPreferenceUtils.getToken();
    if (token.isPresent()) {

      String tokString = token.get();
      Form headers  = (Form) fareProfile.getRequestAttributes().get("org.restlet.http.headers");
      if (headers == null) {     
        headers = new Form();
        fareProfile.getRequestAttributes().put("org.restlet.http.headers", headers);
      }
      headers.set("X-SuperCab-Token", tokString);
    }

    try {
      Representation jacksonRep;
      try {
        jacksonRep = ServerUtilities.convertFareToJsonRepresentation(fare);
      } catch (IOException e1) {
        e1.printStackTrace();
        mException = e1;
        return Optional.absent();
      }
      Representation rep = fareProfile.post(jacksonRep);
      if (fareProfile.getStatus().isSuccess()) {

        FareRepresentation receivedFare = new FareRepresentation(rep);
        Optional<Fare> optionalFare = receivedFare.getFare();
        if (optionalFare.isPresent()) {
          return Optional.of(optionalFare.get().superCabId);
        } else {
//          Log.d(TAG, "response: " + rep.getText());
          return Optional.absent();
        }
      }
    } catch (ResourceException e) {
      e.printStackTrace();
      mException = e;
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<String> fareId) {
    if (mException != null) {
      mListener.handleError(mException);
    }
    mListener.completed(fareId);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  private URI getURI() {
    try {
      String serverUrl = CommonUtilities.SERVER_URL + "/fare";
      //String serverUrl = "http://requestb.in/pijt1epi";
      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

}
