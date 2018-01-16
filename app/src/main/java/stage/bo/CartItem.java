package stage.bo;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import stage.utils.DBHelper;

/**
 * Created by flohi on 12/01/2018.
 */

public class CartItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private String picture;
    private int quantity;
    private String restaurantId;

    public CartItem() {

    }

    public CartItem(String id, String name, String description, double price, String picture, int quantity, String restaurantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.picture = picture;
        this.quantity = quantity;
        this.restaurantId = restaurantId;
    }

    public CartItem getDate(String id, Context context) {
        CartItem cartItem = null;
        DBHelper mydb = new DBHelper(context);
        Cursor cursor = mydb.getData(id);
        if(cursor.moveToFirst()) {
            int index2 = cursor.getColumnIndex(DBHelper.CART_COLUMN_NAME);
            int index3 = cursor.getColumnIndex(DBHelper.CART_COLUMN_DESCRIPTION);
            int index4 = cursor.getColumnIndex(DBHelper.CART_COLUMN_PRICE);
            int index5 = cursor.getColumnIndex(DBHelper.CART_COLUMN_PICTURE);
            int index6 = cursor.getColumnIndex(DBHelper.CART_COLUMN_QUANTITY);
            int index7 = cursor.getColumnIndex(DBHelper.CART_COLUMN_RESTAURANT_ID);

            String name = cursor.getString(index2);
            String description = cursor.getString(index3);
            double price = cursor.getDouble(index4);
            String picture = cursor.getString(index5);
            int quantity = cursor.getInt(index6);
            String restaurantId = cursor.getString(index7);
            cartItem = new CartItem(id, name, description, price, picture, quantity, restaurantId);
        }
        return cartItem;
    }

    public List<CartItem> getAllData(Context context) {
        DBHelper mydb = new DBHelper(context);
        Cursor cursor = mydb.getAllMenus();
        ArrayList<CartItem> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(DBHelper.CART_COLUMN_ID);
            int index2 = cursor.getColumnIndex(DBHelper.CART_COLUMN_NAME);
            int index3 = cursor.getColumnIndex(DBHelper.CART_COLUMN_DESCRIPTION);
            int index4 = cursor.getColumnIndex(DBHelper.CART_COLUMN_PRICE);
            int index5 = cursor.getColumnIndex(DBHelper.CART_COLUMN_PICTURE);
            int index6 = cursor.getColumnIndex(DBHelper.CART_COLUMN_QUANTITY);
            int index7 = cursor.getColumnIndex(DBHelper.CART_COLUMN_RESTAURANT_ID);


            String id = cursor.getString(index);
            String name = cursor.getString(index2);
            String description = cursor.getString(index3);
            double price = cursor.getDouble(index4);
            String picture = cursor.getString(index5);
            int quantity = cursor.getInt(index6);
            String restaurantId = cursor.getString(index7);
            CartItem cartItem = new CartItem(id, name, description, price, picture, quantity, restaurantId);
            list.add(cartItem);
        }
        return list;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(int quantity, Context context) {
        this.quantity = quantity;
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.updateMenuQuantity(this.id, quantity);
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id : ").append(id).append(", ")
                .append("name : ").append(name).append(", ")
                .append("description : ").append(description).append(", ")
                .append("price : ").append(price).append(", ")
                .append("picture : ").append(picture).append(", ")
                .append("quantity : ").append(quantity).toString();
    }
}
