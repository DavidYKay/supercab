package co.gargoyle.supercab.android.tasks;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.os.AsyncTask;
import co.gargoyle.supercab.android.model.Fare;

public class UploadFareTask extends AsyncTask<Fare, Integer, Boolean> {

  //private UploadPhoneCallListener mListener;

  //public UploadPhoneCallTask(UploadPhoneCallListener listener) {
  //  mListener = listener;
  //}

  @Override
  protected Boolean doInBackground(Fare... fares) {
    Fare fare = fares[0];
    
    
    return false;
  }

  @Override
  protected void onPostExecute(Boolean success) {
//    mListener.completed(success);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    
  }

}
