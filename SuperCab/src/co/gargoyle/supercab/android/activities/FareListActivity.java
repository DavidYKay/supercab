package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;

import com.google.inject.Inject;

public class FareListActivity extends RoboListActivity {
    
  private static final String[] items={"lorem", "ipsum", "dolor",
    "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
    "ligula", "vitae", "arcu", "aliquet", "mollis",
    "etiam", "vel", "erat", "placerat", "ante",
    "porttitor", "sodales", "pellentesque", "augue", "purus"}; 
  
  @Inject
  private BroadcastUtils mBroadcastUtils;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fare_list);

    setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

    getData();
  }


  private void getData() {
    mBroadcastUtils.broadcastUploadData();
  }

}
