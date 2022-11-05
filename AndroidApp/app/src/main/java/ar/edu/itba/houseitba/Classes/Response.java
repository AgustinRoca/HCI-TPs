package ar.edu.itba.houseitba.Classes;

import com.google.gson.annotations.SerializedName;

import ar.edu.itba.houseitba.Classes.Devices.Device;

public class Response {
    @SerializedName("result")
    public Object result;

    public Response(){}
}
