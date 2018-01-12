package stage.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import fr.hirsonf.stage.R;
import stage.bo.Menu;
import stage.utils.DBHelper;
import stage.utils.GlideApp;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by flohi on 10/01/2018.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private HashMap<String,Menu> menuList;
    private Context context;
    private List<String> menuIdsList;
    private String restaurantId;

    public MenuAdapter(HashMap<String,Menu> menuList, Context context, List<String> menuIdsList, String restaurantId) {
        this.menuList = menuList;
        this.context = context;
        this.menuIdsList = menuIdsList;
        this.restaurantId = restaurantId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        Log.w("MenuAdapter", "View created");

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, final int position) {
        String key = (String) menuList.keySet().toArray()[position];
        final Menu menu = menuList.get(key);
        holder.menuName.setText(menu.getName());
        holder.menuDescription.setText(menu.getDescription());
        holder.menuPrice.setText("Price : " + menu.getPrice() + " £");
        holder.menuTime.setText("Waiting time : " + menu.getTime() + " min");
        StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference("restaurantResources")
                .child(restaurantId)
                .child(menuIdsList.get(position))
                .child("photo.jpg");

        GlideApp.with(context)
                .load(storageReference)
                .centerCrop()
                .error(R.color.accent)
                .into(holder.menuPicture);

        Log.w("MenuAdapter", "Data binded");
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = menuIdsList.get(position);
                DBHelper mydb = new DBHelper(context);
                mydb.insertMenu(id, menu.getName(), menu.getDescription(), menu.getPrice(), menu.getPicture(), restaurantId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public List<String> getMenuIdsList() {
        return menuIdsList;
    }

    public void setMenuIdsList(List<String> menuIdsList) {
        this.menuIdsList = menuIdsList;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public HashMap<String,Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(HashMap<String,Menu> restaurantList) {
        this.menuList = restaurantList;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private TextView menuName, menuDescription, menuPrice, menuTime;
        private ImageView menuPicture;
        private Button addToCart;


        private ViewHolder(View v) {
            super(v);
            menuName = (TextView) v.findViewById(R.id.menuName);
            menuDescription = (TextView) v.findViewById(R.id.menuDescription);
            menuPrice = (TextView) v.findViewById(R.id.menuPrice);
            menuTime = (TextView) v.findViewById(R.id.menuTime);
            menuPicture = (ImageView) v.findViewById(R.id.menuPicture);
            addToCart = (Button) v.findViewById(R.id.add_to_cart);
        }

    }

}

