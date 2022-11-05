package ar.edu.itba.houseitba.Classes.Devices;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceResponse {
    @SerializedName("devices")
    public List<Device> devices;
}
