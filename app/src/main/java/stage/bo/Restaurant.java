package stage.bo;

import java.util.ArrayList;

/**
 * Created by flohi on 04/01/2018.
 */

public class Restaurant {

    private int waitingTime;
    private double distance;
    private double averagePrice;
    private String name;
    private String description;
    private String picture;
    private ArrayList<Menu> restaurantMenus;



    public Restaurant(String name, double distance) {
        this.name = name;

        this.distance = distance;
    }

    public Restaurant(int waitingTime, double distance, double averagePrice, String name, String description, String picture, ArrayList<Menu> restaurantMenus) {
        this.waitingTime = waitingTime;
        this.distance = distance;
        this.averagePrice = averagePrice;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.restaurantMenus = restaurantMenus;
    }

    public ArrayList<Menu> getRestaurantMenus() {
        return restaurantMenus;
    }

    public void setRestaurantMenus(ArrayList<Menu> restaurantMenus) {
        this.restaurantMenus = restaurantMenus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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
        return "Restaurant : " + this.getName() + ", distance to position : " + this.getDistance();
    }
}
