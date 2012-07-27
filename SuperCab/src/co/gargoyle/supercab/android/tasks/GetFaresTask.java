package co.gargoyle.supercab.android.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.content.Context;
import android.os.AsyncTask;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.tasks.listeners.GetFaresListener;
import co.gargoyle.supercab.android.utilities.CommonUtils;
import co.gargoyle.supercab.android.utilities.ServerUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class GetFaresTask extends AsyncTask<Map<String, Object>, Integer, List<Fare>> {

  @SuppressWarnings("unused")
  private static final String TAG = "GetFaresTask";

  private GetFaresListener mListener;
  private Context mContext;
  private Exception mException;

  public GetFaresTask(Context context, GetFaresListener listener) {
    mListener = listener;
    mContext = context;
  }

  @Override
  protected List<Fare> doInBackground(Map<String, Object>... paramsList) {

    Map<String, Object> params = paramsList[0];

    FareStatus status = (FareStatus) params.get("status");
    URI uri = getURI(status);

    ClientResource fareProfile = new ClientResource(uri);
    ServerUtils.addAuthHeaderToClientResource(mContext, fareProfile);
    try {
      Representation rep = fareProfile.get();
      if (fareProfile.getStatus().isSuccess()) {
        List<Fare> fares = parseFaresFromRepresentation(rep);
        return fares;
      }
    } catch (ResourceException ex) {
      ex.printStackTrace();
      mException = ex;
    }

    return new ArrayList<Fare>();
  }

  @Override
  protected void onPostExecute(List<Fare> fares) {
    mListener.completed(fares);
    
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
  
  private URI getURI() {
    return getURI(null);
  }

  private URI getURI(FareStatus status) {
    try {
      String serverUrl = CommonUtils.SERVER_URL + "/fare";
      if (status != null) {
        serverUrl += "/status/" + status.toString();
      }

      URI uri = new URI(serverUrl);
      return uri;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Programmer mistyped the URI!");
    }
  }

  private List<Fare> parseFaresFromRepresentation(Representation rep) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    JavaType type = TypeFactory.defaultInstance().constructParametricType(List.class, Fare.class);
    ObjectReader reader = mapper.reader(type);

    try {
      return reader.readValue(rep.getText());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      mException = e;
    } catch (IOException e) {
      e.printStackTrace();
      mException = e;
    }

    return new ArrayList<Fare>();
  }

}
