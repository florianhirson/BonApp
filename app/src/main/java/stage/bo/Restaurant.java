package stage.bo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by flohi on 04/01/2018.
 */

public class Restaurant {

    private int waitingTime;
    private double averagePrice;
    private String name;
    private String description;
    private String picture;
    private HashMap<String,Menu> restaurantMenus;

    public Restaurant() {
    }

    public Restaurant(String name) {
        this.name = name;
    }

    public Restaurant(int waitingTime,  double averagePrice, String name, String description, String picture, HashMap<String,Menu> restaurantMenus) {
        this.waitingTime = waitingTime;
        this.averagePrice = averagePrice;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.restaurantMenus = restaurantMenus;
    }

    public HashMap<String,Menu> getRestaurantMenus() {
        return restaurantMenus;
    }

    public void setRestaurantMenus(HashMap<String,Menu> restaurantMenus) {
        this.restaurantMenus = restaurantMenus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "Restaurant : " + this.getName()
                + "\nwaiting time : " + waitingTime
                + "\naverage price : " + averagePrice
                + "\ndescription : " + description
                + "\nmenus" + restaurantMenus;
    }
}
