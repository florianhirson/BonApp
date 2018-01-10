package stage.bo;

/**
 * Created by flohi on 09/01/2018.
 */

public class Menu {
    private String description;
    private String name;
    private String picture;
    private double price;
    private int time;

    public Menu() {
    }

    public Menu(String description, String name, String picture, double price, int time) {
        this.description = description;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "\n menu's name : " + name + ", description : " + description + ", price : " + price + ", waiting time : " + time;
    }
}
