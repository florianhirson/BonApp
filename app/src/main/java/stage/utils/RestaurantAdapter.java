package stage.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import fr.hirsonf.stage.R;
import stage.bo.Restaurant;

/**
 * Created by flohi on 04/01/2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{
    private static final int UNSELECTED = -1;
    private List<Restaurant> restaurantList;
    private static int selectedItem = UNSELECTED;
    private RecyclerView recyclerView;


    //constructor, call on creation
    public RestaurantAdapter(ArrayList<Restaurant> restaurantList, RecyclerView recyclerView) {
        this.restaurantList = restaurantList;
        this.recyclerView = recyclerView;
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
        holder.name.setText(restaurant.getName());
        holder.distance.setText("" + restaurant.getDistance());
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
        private ExpandableLayout expandableLayout;
        private RelativeLayout relativeLayout;


        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.listTitle);
            distance = (TextView) v.findViewById(R.id.listDistance);
            expandableLayout = v.findViewById(R.id.expandable_layout);
            relativeLayout = v.findViewById(R.id.relative_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.w("RestaurantAdapter", "View clicked");
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                relativeLayout.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                relativeLayout.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }



        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("ExpandableLayout", "State: " + state);
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }

        public void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;

            relativeLayout.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

    }


}
