package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.content.Context;
import android.os.AsyncTask;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.tasks.listeners.GetFareListener;
import co.gargoyle.supercab.android.utilities.CommonUtils;
import co.gargoyle.supercab.android.utilities.ServerUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Optional;

public class GetFareTask extends AsyncTask<String, Integer, Optional<Fare>> {

  @SuppressWarnings("unused")
  private static final String TAG = "GetFareTask";

  private GetFareListener mListener;
  private Context mContext;
  private Exception mException;

  public GetFareTask(Context context, GetFareListener listener) {
    mListener = listener;
    mContext = context;
  }

  @Override
  protected Optional<Fare> doInBackground(String... paramsList) {
    String id = paramsList[0];

    URI uri = getURI(id);

    ClientResource fareProfile = new ClientResource(uri);
    ServerUtils.addAuthHeaderToClientResource(mContext, fareProfile);
    try {
      Representation rep = fareProfile.get();
      if (fareProfile.getStatus().isSuccess()) {
        return parseFareFromRepresentation(rep);
      }
    } catch (ResourceException ex) {
      ex.printStackTrace();
      mException = ex;
    }

    return Optional.absent();
  }

  @Override
  protected void onPostExecute(Optional<Fare> fare) {
    mListener.completed(fare);

    if (mException != null) {
      if (mException instanceof ResourceException) {
        ResourceException resEx = (ResourceException) mException;
        if (resEx.getStatus().getCode() == 401) {
          mListener.unauthorized();
          return;
        }
      }
      mListener.handleError(mException);
    }
  }

  @Override
  protected void onProgressUpdate(Integer... values) {

  }

  private URI getURI(String id) {
    try {
      String serverUrl = CommonUtils.SERVER_URL + "/fare/" + id;

      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

  private Optional<Fare> parseFareFromRepresentation(Representation rep) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    ObjectReader reader = mapper.reader(Fare.class);

    try {
      Fare fare = reader.readValue(rep.getText());
      return Optional.of(fare);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      mException = e;
    } catch (IOException e) {
      e.printStackTrace();
      mException = e;
    }

    return Optional.absent();
  }
}
