package stage.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.hirsonf.stage.R;
import stage.bo.Restaurant;

/**
 * Created by flohi on 04/01/2018.
 */

public class RestaurantAdapter extends ArrayAdapter<Restaurant>{

    private Context context;
    private List<Restaurant> restaurantList;

    //constructor, call on creation
    public RestaurantAdapter(Context context, int resource, ArrayList<Restaurant> restaurantList) {
        super(context, resource);
        this.context = context;
        this.restaurantList =restaurantList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the property we are displaying
        Restaurant restaurant = restaurantList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_layout, null);
        }


        TextView name = (TextView) convertView.findViewById(R.id.listTitle);
        TextView distance = (TextView) convertView.findViewById(R.id.listDistance);

        name.setText(restaurant.getName());
        distance.setText("" + restaurant.getDistance());
        Log.w("RestaurantAdapter", "inflating " + restaurant);

        return convertView;
    }

    @Override
    public void add(Restaurant object) {


        restaurantList.add(object);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }
}
