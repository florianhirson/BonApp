package stage.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by flohi on 12/01/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cart";
    public static final String CART_TABLE_NAME = "cart";
    public static final String CART_COLUMN_ID = "id";
    public static final String CART_COLUMN_NAME = "name";
    public static final String CART_COLUMN_DESCRIPTION = "description";
    public static final String CART_COLUMN_PRICE = "price";
    public static final String CART_COLUMN_PICTURE = "picture";
    public static final String CART_COLUMN_QUANTITY = "quantity";
    public static final String CART_COLUMN_RESTAURANT_ID = "restaurantId";
    private HashMap hp;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS cart " +
                        "(id text primary key, name text, description text, price real, picture text, quantity integer, restaurantId text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    public boolean insertMenu (String id, String name, String description, double price, String picture, String restaurantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("price", price);
        contentValues.put("picture", picture);
        contentValues.put("quantity", 1);
        contentValues.put("restaurantId", restaurantId);

        int code = (int) db.insertWithOnConflict("cart", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (code == -1) {
            Cursor res = db.rawQuery("select quantity from cart where id = '" + id + "'", null);
            res.moveToFirst();
            int quantity = Integer.valueOf(res.getString(0));
            res.close();
            ++quantity;
            updateMenuQuantity(id, quantity);
        }
        return true;
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from cart where id="+id+"", null );
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CART_TABLE_NAME);
    }

    public boolean updateMenu (String id, String name, String description, int quantity, double price, String picture, String restaurantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("price", price);
        contentValues.put("picture", picture);
        contentValues.put("quantity", 1);
        contentValues.put("restaurantId", restaurantId);
        db.update("cart", contentValues, "id = ? ", new String[] { id } );
        return true;
    }

    public void updateMenuQuantity (String id,  int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("quantity", quantity);
        db.update("cart", contentValues, "id = ? ", new String[] { id } );
    }

    public Integer deleteMenu (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart",
                "id = ? ",
                new String[] { id });
    }

    public void emptyCart() {
        onUpgrade(this.getReadableDatabase(), 0, 1);

    }



    public Cursor getAllMenus() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from cart", null );
    }




}
