package stage.bo;

/**
 * Created by flohi on 04/01/2018.
 */

public class Restaurant {
    private String name;
    private double distance;

    public Restaurant(String name, double distance) {
        this.name = name;
        this.distance = distance;
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

    @Override
    public String toString() {
        return "Restaurant : " + this.getName() + ", distance to position : " + this.getDistance();
    }
}
