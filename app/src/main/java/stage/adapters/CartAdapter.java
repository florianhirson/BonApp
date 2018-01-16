package stage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import fr.hirsonf.stage.R;
import stage.bo.CartItem;
import stage.bo.Menu;
import stage.utils.DBHelper;
import stage.utils.GlideApp;

/**
 * Created by flohi on 12/01/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> list;
    private Context context;

    public CartAdapter(List<CartItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        Log.w("CartAdapter", "View created");

        return new CartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {
        final CartItem cartItem = list.get(position);
        holder.menuName.setText(cartItem.getName());
        holder.menuDescription.setText(cartItem.getDescription());
        holder.menuPrice.setText("Price : " + cartItem.getPrice() + " £");
        holder.materialNumberPicker.setValue(cartItem.getQuantity());
        StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference("restaurantResources")
                .child(cartItem.getRestaurantId())
                .child(cartItem.getId())
                .child("photo.jpg");

        GlideApp.with(context)
                .load(storageReference)
                .centerCrop()
                .error(R.color.accent)
                .into(holder.menuPicture);

        Log.w("MenuAdapter", "Data binded");


        holder.materialNumberPicker.setOnValueChangedListener(
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        cartItem.setQuantity(newVal, context);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private TextView menuName, menuDescription, menuPrice;
        private ImageView menuPicture;
        private MaterialNumberPicker materialNumberPicker;


        private ViewHolder(View v) {
            super(v);
            menuName = (TextView) v.findViewById(R.id.menuName);
            menuDescription = (TextView) v.findViewById(R.id.menuDescription);
            menuPrice = (TextView) v.findViewById(R.id.menuPrice);
            menuPicture = (ImageView) v.findViewById(R.id.menuPicture);
            materialNumberPicker = (MaterialNumberPicker) v.findViewById(R.id.material_number_picker);
        }

    }
}
