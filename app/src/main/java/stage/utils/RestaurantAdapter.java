package stage.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import fr.hirsonf.stage.R;
import fr.hirsonf.stage.activities.RestaurantActivity;
import stage.bo.Restaurant;

/**
 * Created by flohi on 04/01/2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{
    private static final int UNSELECTED = -1;
    private List<Integer> distanceList;
    private List<Restaurant> restaurantList;
    private List<String> idList;
    private static int selectedItem = UNSELECTED;
    private RecyclerView recyclerView;
    private Context context;



    //constructor, call on creation
    public RestaurantAdapter(ArrayList<Restaurant> restaurantList, RecyclerView recyclerView, Context context, ArrayList<String> idList, List<Integer> distanceList) {
        this.restaurantList = restaurantList;
        this.idList = idList;
        this.recyclerView = recyclerView;
        this.context = context;
        this.distanceList = distanceList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        Log.w("RestaurantAdapter", "View created");

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        int distance = distanceList.get(position);
        holder.name.setText(restaurant.getName());
        holder.distance.setText(distance + " m");
        Log.w("RestaurantAdapter", "Data binded");

        holder.bind();
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener{
        // each data item is just a string in this case

        private TextView name, distance;
        private Button details;
        private ExpandableLayout expandableLayout;
        private LinearLayout layoutTextView;
        private ViewHolder holder;



        private ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.listTitle);
            distance = (TextView) v.findViewById(R.id.listDistance);
            expandableLayout = v.findViewById(R.id.expandable_layout);
            layoutTextView = v.findViewById(R.id.layoutTextView);
            details = (Button) v.findViewById(R.id.details);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            layoutTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Log.w("RestaurantAdapter", "View clicked");
            final ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);

            if(holder != null) {
                holder.layoutTextView.setSelected(false);
                holder.expandableLayout.collapse();
            }



            if (position == selectedItem) {
                selectedItem = UNSELECTED;

            } else {
                layoutTextView.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, RestaurantActivity.class);
                        String message = "Id : " + idList.get(position) + " clicked";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        layoutTextView.setSelected(false);
                        expandableLayout.collapse();
                    }
                });
            }
        }



        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("ExpandableLayout", "State: " + state);
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }

        private void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;

            layoutTextView.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

    }


}
