package co.gargoyle.supercab.android.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import co.gargoyle.supercab.android.model.Fare;

public class FareListAdapter extends BaseAdapter {

  private List<Fare> mFares;
  private LayoutInflater mLayoutInflater;

  public FareListAdapter(LayoutInflater layoutInflater, List<Fare> events) {
    mFares = events;
    mLayoutInflater = layoutInflater;
  }
  
  @Override
  public int getCount() {
    return mFares.size();
  }

  @Override
  public Object getItem(int position) {
    return mFares.get(position);
  }

  @Override
  public long getItemId(int position) {
    Fare event = mFares.get(position);
    return event.id;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    
    if (convertView == null) {
      convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
    }
    
    Fare fare = mFares.get(position);
    
    TextView contentText = (TextView) convertView.findViewById(android.R.id.text1);
    contentText.setText(fare.source.toString());
    
    TextView phoneText = (TextView) convertView.findViewById(android.R.id.text2);
    phoneText.setText(fare.destination.toString());

    //RowWrapper wrapper = new RowWrapper(row);
    //row.setTag(wrapper);

    return convertView;
  }

}
