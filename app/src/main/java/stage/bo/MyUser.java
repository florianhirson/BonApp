package stage.bo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by flohi on 08/11/2017.
 */

@IgnoreExtraProperties
public class MyUser {

    private String name;
    private String address;
    private String mobile;
    private String birthdate;
    private String email;

    public MyUser() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name : " + getName() + "\nAddress : " + getAddress() + "\nMobile : " + getMobile() + "\nMail : " + getEmail() + "\nBirthdate : " +getBirthdate();
    }
}
