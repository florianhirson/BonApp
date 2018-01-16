package fr.hirsonf.stage.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.hirsonf.stage.R;
import stage.adapters.CartAdapter;
import stage.adapters.MenuAdapter;
import stage.bo.CartItem;
import stage.bo.Menu;
import stage.utils.DBHelper;

public class CartActivity extends AppCompatActivity {
    private String TAG = CartActivity.class.getSimpleName();
    private CartAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_cart) Button emptyCart;
    @BindView(R.id.checkout) Button checkout;
    @BindView(R.id.cart_is_empty) TextView cartIsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        final List<CartItem> list = new CartItem().getAllData(getApplicationContext());

        layoutManager = new LinearLayoutManager(this);
        adapter = new CartAdapter(list, getApplicationContext());

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "cart : " + list);

        if(list.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            cartIsEmpty.setVisibility(View.VISIBLE);
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CartActivity.this, "Coming soon ! ", Toast.LENGTH_SHORT).show();
            }
        });

        emptyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                alertDialogBuilder.setTitle("Warning");
                alertDialogBuilder.setIcon(CartActivity.this.getResources().getDrawable(R.drawable.ic_radiation, null));
                alertDialogBuilder.setMessage("Are you sure to empty the cart ?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        dbHelper.emptyCart();
                        list.clear();
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.INVISIBLE);
                        cartIsEmpty.setVisibility(View.VISIBLE);
                    }
                });
                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



    }
}
