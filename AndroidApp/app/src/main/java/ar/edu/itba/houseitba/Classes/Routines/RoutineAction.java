package ar.edu.itba.houseitba.Classes.Routines;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class RoutineAction {
    @SerializedName("id")
    private String id;
    @SerializedName("actionName")
    private String name;
    @SerializedName("params")
    private List<String> parameters;
    @SerializedName("meta")
    private HashMap<String, String> meta;
}