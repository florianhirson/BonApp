package fr.hirsonf.stage.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.carteasy.v1.lib.Carteasy;

import java.util.Map;

import fr.hirsonf.stage.R;
import stage.bo.Menu;
import stage.utils.DBHelper;

public class CartActivity extends AppCompatActivity {
    private String TAG = CartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        DBHelper mydb = new DBHelper(this);
        Cursor res = mydb.getAllMenus();
        res.moveToFirst();

        while(!res.isAfterLast()){
            String text = "";
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < res.getColumnCount(); ++i) {
                sb.append(res.getColumnName(i));
                sb.append(" : ");
                sb.append(res.getString(i));
                sb.append(", ");
            }
            text = sb.toString();
            Log.d(TAG, text);
            res.moveToNext();
        }

    }
}
