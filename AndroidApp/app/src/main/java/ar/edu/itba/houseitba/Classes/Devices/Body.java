package ar.edu.itba.houseitba.Classes.Devices;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Body {

    @SerializedName("name")
    private String name;
    @SerializedName("meta")
    private HashMap<String, String> meta;

    public Body(String name){
        this.name = name;
        this.meta = new HashMap<>();
    }

    public Body(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, String> meta) {
        this.meta = meta;
    }

    public void addMeta(String key, String obj){
        this.meta.putIfAbsent(key, obj);
    }
}
