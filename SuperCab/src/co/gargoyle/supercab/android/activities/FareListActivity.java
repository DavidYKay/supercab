package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class FareListActivity extends RoboListActivity {
    
  private static final String[] items={"lorem", "ipsum", "dolor",
    "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
    "ligula", "vitae", "arcu", "aliquet", "mollis",
    "etiam", "vel", "erat", "placerat", "ante",
    "porttitor", "sodales", "pellentesque", "augue", "purus"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //setContentView(R.layout.fare_list);
    setListAdapter(new ArrayAdapter<String>(this,
                                            android.R.layout.simple_list_item_1,
                                            items));

  }

}
