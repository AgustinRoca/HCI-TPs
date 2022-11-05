package ar.edu.itba.houseitba.Classes.Routines;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Routine{

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("actions")
    private List<RoutineAction> actions;
    @SerializedName("meta")
    private HashMap<String, String> meta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoutineAction> getActions() {
        return actions;
    }

    public void setActions(List<RoutineAction> actions) {
        this.actions = actions;
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, String> meta) {
        this.meta = meta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id);
    }

}
