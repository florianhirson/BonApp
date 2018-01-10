package fr.hirsonf.stage.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.hirsonf.stage.R;
import stage.bo.Menu;
import stage.bo.Restaurant;
import stage.utils.GlideApp;
import stage.utils.MenuAdapter;
import stage.utils.RestaurantAdapter;

public class RestaurantActivity extends AppCompatActivity {

    private int distance;
    private String id;
    private String name;
    private String TAG = RestaurantActivity.class.getSimpleName();

    private ArrayList<String> menuIdsList;
    private HashMap<String, Menu> menuHashMap;
    private MenuAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Restaurant restaurant;

    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.distance) TextView tDistance;
    @BindView(R.id.average_price) TextView tAveragePrice;
    @BindView(R.id.waiting_time) TextView tWaitingTime;
    @BindView(R.id.show_on_map) Button showOnMap;
    @BindView(R.id.show_itinary) Button showItinary;
    @BindView(R.id.restaurant_picture) ImageView restaurantPicture;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        menuHashMap = new HashMap<>();
        menuIdsList = new ArrayList<>();



        if(b!=null)
        {
            distance =(int) b.get("distance");
            id = (String) b.get("id");
            name = (String) b.get("name");

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            adapter = new MenuAdapter(menuHashMap, this,  menuIdsList, id);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(false);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

            tDistance.setText("Distance : " + distance + " m");
            restaurantName.setText(name);

            DatabaseReference databaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference("restaurants")
                    .child(id);

            StorageReference storageReference = FirebaseStorage
                    .getInstance()
                    .getReference("restaurantResources")
                    .child(id)
                    .child("photo.jpg");

            GlideApp.with(this)
                    .load(storageReference)
                    .centerCrop()
                    .error(R.color.accent)
                    .into(restaurantPicture);

            Log.d(TAG, "restaurant's id : " + id);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    restaurant = dataSnapshot.getValue(Restaurant.class);
                    Log.d(TAG, restaurant.toString());
                    tAveragePrice.setText("Average price : " + restaurant.getAveragePrice() + " £");
                    tWaitingTime.setText("Average waiting time : " + restaurant.getWaitingTime() + " min");
                    menuHashMap.putAll(restaurant.getRestaurantMenus());
                    for(Map.Entry<String, Menu> entry : menuHashMap.entrySet()) {
                        String key = entry.getKey();
                        Menu value = entry.getValue();
                        menuIdsList.add(key);
                        // do what you have to do here
                        // In your case, another loop.
                    }
                    adapter.notifyDataSetChanged();



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Download restaurant's data : failure " + databaseError.toException());
                }
            });
        }
    }
}
