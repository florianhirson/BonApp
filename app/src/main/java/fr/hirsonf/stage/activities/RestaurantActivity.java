package fr.hirsonf.stage.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.hirsonf.stage.R;

public class RestaurantActivity extends AppCompatActivity {

    private int distance;
    private String id;
    private String name;

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

        if(b!=null)
        {
            distance =(int) b.get("distance");
            id = (String) b.get("id");
            name = (String) b.get("name");

            tDistance.setText("Distance : " + distance);
            restaurantName.setText("Name : " + name);
        }
    }
}
