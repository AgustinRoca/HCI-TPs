package ar.edu.itba.houseitba.Classes.Routines;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoutineResponse {
    @SerializedName("result")
    public List<Routine> routines;
}
