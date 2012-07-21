package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import android.os.AsyncTask;
import android.util.Log;
import co.gargoyle.supercab.android.model.Fare;

import com.google.common.base.Optional;

import com.google.common.base.Optional;

public class UploadFareTask extends AsyncTask<Fare, Integer, Optional<Long>> {

   private UploadFareListener mListener;

   public UploadFareTask(UploadFareListener listener) {
     mListener = listener;
   }

  private static final String TAG = "UploadFareTask";

  @Override
  protected Optional<Long> doInBackground(Fare... fares) {
    Fare fare = fares[0];

    URI uri = getURI();

    ClientResource fareProfile = new ClientResource(uri);

    JacksonRepresentation<Fare> jacksonRep = new JacksonRepresentation<Fare>(MediaType.APPLICATION_JSON, fare);
    //Representation rep = fareProfile.post();
    Representation rep = fareProfile.post(jacksonRep);
    if (fareProfile.getStatus().isSuccess()) {
      try {
        Log.d(TAG, "response: " + rep.getText());
        return Optional.of(0L);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<Long> fareId) {
    mListener.completed(fareId);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  private URI getURI() {
    try {
      URI uri = new URI("http://192.168.0.107/api/v1/fare");
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

}
