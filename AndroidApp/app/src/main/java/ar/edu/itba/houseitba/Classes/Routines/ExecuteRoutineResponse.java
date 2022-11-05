package ar.edu.itba.houseitba.Classes.Routines;

import com.google.gson.annotations.SerializedName;

public class ExecuteRoutineResponse {
    @SerializedName("result")
    public Boolean[] result;

    @SerializedName("routineId")
    public String id;
}
