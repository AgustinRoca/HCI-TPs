package ar.edu.itba.houseitba.WebService;

import java.util.HashMap;

import ar.edu.itba.houseitba.Classes.Action;
import ar.edu.itba.houseitba.Classes.Devices.DeviceResponse;
import ar.edu.itba.houseitba.Classes.Devices.OneDeviceResponse;
import ar.edu.itba.houseitba.Classes.Response;
import ar.edu.itba.houseitba.Classes.Routines.ExecuteRoutineResponse;
import ar.edu.itba.houseitba.Classes.Routines.RoutineResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Webservice {

    @GET("devices")
    Call<DeviceResponse> getDevices();

    @GET("devices/{id}")
    Call<OneDeviceResponse> getDeviceById(@Path("id") String id);

    @GET("routines")
    Call<RoutineResponse> getRoutines();

    @PUT("devices/{id}")
    Call<Void> updateDevice(@Path("id") String id, @Body ar.edu.itba.houseitba.Classes.Devices.Body body);

    @PUT("devices/{id}/{action}")
    Call<Response> doActionWithParams(@Path("id") String id, @Path("action") String action, @Body Action.ActionBody body);

    @PUT("devices/{id}/{action}")
    Call<Response> doAction(@Path("id") String id, @Path("action") String action);

    @PUT("devices/{id}/{action}")
    Call<Response> doActionWithExtendedParams(@Path("id") String id, @Path("action") String action, @Body Action.ExtendedActionBody body);

    @PUT("routines/{id}/execute")
    Call<ExecuteRoutineResponse> executeRoutine(@Path("id") String id);


}
