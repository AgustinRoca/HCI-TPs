package ar.edu.itba.houseitba.Classes.Devices;

import com.google.gson.annotations.SerializedName;

public class DeviceType {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;

    public DeviceType(){}
}
