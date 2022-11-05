package ar.edu.itba.houseitba.Classes.Devices;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Objects;

import ar.edu.itba.houseitba.Utils.Constants;

public class Device {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("meta")
    private HashMap<String, String> meta;
    @SerializedName("type")
    private DeviceType type;
    @SerializedName("state")
    private DeviceState state;

    Device(String name, String id){
        this.name = name;
        this.id = id;
        this.meta = new HashMap<>();
    }

    Device(){ }

    public Body getBodyForUpdate(){
        Body body = new Body(this.name);
        for (String key : this.meta.keySet()){
            body.addMeta(key, this.meta.get(key));
        }
        return body;
    }

    public void addMeta(String key, String obj){
        if (this.meta.containsKey(key)){
            this.changeMeta(key, obj);
        } else {
            this.meta.putIfAbsent(key, obj);
        }
    }

    public String getMeta(String key){
        return this.meta.get(key);
    }

    public void changeMeta(String key, String obj){
        this.meta.replace(key, obj);
    }

    public String getName(){ return this.name; }

    public void setName(String name){ this.name = name; }

    public String getId(){ return this.id; }

    public void setId(String id){ this.id = id;}

    public String getTypeId(){ return this.type.id;}

    public String getTypeName(){ return this.type.name;}

    public DeviceState getState(){ return this.state; }

    public String getStateString(){
        return "";
    }

    public boolean isFav(){
        if (this.meta != null){
            return Boolean.parseBoolean(this.meta.get("fav"));
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(name, device.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public class DeviceState {
        @SerializedName("status")
        public String status;
        @SerializedName("temperature")
        public int temperature;
        @SerializedName("mode")
        public String mode;
        @SerializedName("verticalSwing")
        public String verticalSwing;
        @SerializedName("horizontalSwing")
        public String horizontalSwing;
        @SerializedName("fanSpeed")
        public String fanSpeed;
        @SerializedName("heat")
        public String heat;
        @SerializedName("grill")
        public String grill;
        @SerializedName("convection")
        public String convection;
        @SerializedName("color")
        public String color;
        @SerializedName("brightness")
        public int brightness;
        @SerializedName("lock")
        public String lock;
        @SerializedName("freezerTemperature")
        public int freezerTemperature;
        @SerializedName("level")
        public int level;
    }
}
