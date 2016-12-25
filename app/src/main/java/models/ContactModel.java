package models;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haseeb on 23/12/16.
 */
public class ContactModel implements Parcelable {

    private String name, email, phone, officePhone;
    private Double latitude = new Double(0), longitude = new Double(0);

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContactModel() {

    }

    public ContactModel(Parcel in) {
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        officePhone = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(officePhone);
        if (latitude!=null) {
            dest.writeDouble(latitude);
        }
        if (longitude != null) {
            dest.writeDouble(longitude);
        }
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<ContactModel> CREATOR = new Creator<ContactModel>() {

        public ContactModel createFromParcel(Parcel in) {
            return new ContactModel(in);
        }

        public ContactModel[] newArray(int size) {
            return new ContactModel[size];
        }
    };
}
