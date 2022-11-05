package ar.edu.itba.houseitba.Classes;

import com.google.gson.annotations.SerializedName;

public class Action {
    public String deviceId;
    public String actionName;
    public Object value;
    public Object extendedValue;

    public Action(String deviceId, String actionName, Object value){
        this.deviceId = deviceId;
        this.actionName = actionName;
        this.value = value;
    }

    public Action(String deviceId, String actionName, Object value, Object extendedValue){
        this.deviceId = deviceId;
        this.actionName = actionName;
        this.value = value;
        this.extendedValue = extendedValue;
    }

    public String getValue(){
        if (this.value != null){
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(this.value);
            sb.append("]");
            return sb.toString();
        } else {
            return "";
        }
    }

    public ActionBody getBody(){
        if (this.value == null){
            return new ActionBody("");
        } else {
            return new ActionBody(this.value);
        }
    }

    public ExtendedActionBody getExtendedBody(){
        if (this.value == null){
            return new ExtendedActionBody("", "");
        } else {
            return new ExtendedActionBody(this.value, this.extendedValue);
        }
    }

    public class ActionBody{
        @SerializedName("0")
        public Object value;

        public ActionBody(Object value){
            this.value = value;
        }
    }

    public class ExtendedActionBody{
        @SerializedName("0")
        public Object value1;
        @SerializedName("1")
        public Object value2;

        public ExtendedActionBody(Object value1, Object value2){
            this.value1 = value1;
            this.value2 = value2;
        }
    }
}
