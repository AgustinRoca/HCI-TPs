package ar.edu.itba.houseitba.Repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.houseitba.Cache.RoutineCache;
import ar.edu.itba.houseitba.Classes.Routines.ExecuteRoutineResponse;
import ar.edu.itba.houseitba.Classes.Routines.Routine;
import ar.edu.itba.houseitba.Classes.Routines.RoutineResponse;
import ar.edu.itba.houseitba.Notifications.Notification;
import ar.edu.itba.houseitba.WebService.WebServiceWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutineRepository extends Repository {
    public static MutableLiveData<List<Routine>> getRoutines(boolean force){
        final MutableLiveData<List<Routine>> data = new MutableLiveData<>();
        List<Routine> cache = RoutineCache.GetRoutineCache();
        if (cache == null || force) {
            if (WebServiceWrapper.GetServiceInstance() != null) {
                WebServiceWrapper.GetServiceInstance().getRoutines().enqueue(new Callback<RoutineResponse>() {
                    @Override
                    public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                        if (response.body() != null){
                            List<Routine> aux = response.body().routines;
                            data.setValue(aux);
                            RoutineCache.StoreCache(aux);
                        } else {
                            data.postValue(new ArrayList<Routine>());
                            Log.e("GET_ROUTINES", "EMPTY BODY");
                        }
                    }

                    @Override
                    public void onFailure(Call<RoutineResponse> call, Throwable t) {
                        data.postValue(new ArrayList<Routine>());
                        Log.e("GET_ROUTINES", t.getMessage());
                    }
                });
            }
        } else {
            data.setValue(cache);
        }

        return data;
    }

    public static void getRoutinesByWorker(final LifecycleOwner lco, final Context context){
        final MutableLiveData<List<Routine>> data = new MutableLiveData<>();

        if (WebServiceWrapper.GetServiceInstance() != null){
            WebServiceWrapper.GetServiceInstance().getRoutines().enqueue(new Callback<RoutineResponse>() {
                @Override
                public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                    if (response.body() != null && RoutineCache.GetRoutineCache() != null){
                        verifyUpdates(context, RoutineCache.GetRoutineCache(), response.body().routines);
                    }
                }

                @Override
                public void onFailure(Call<RoutineResponse> call, Throwable t) {
                    Log.e("GET ROUTINES BY WORKER", t.getMessage());
                }
            });
        }
    }

    public static MutableLiveData<Boolean> runRoutine(String id){
        final MutableLiveData<Boolean> result = new MutableLiveData<>();
        if(WebServiceWrapper.GetServiceInstance() != null){
            WebServiceWrapper.GetServiceInstance().executeRoutine(id).enqueue(new Callback<ExecuteRoutineResponse>() {
                @Override
                public void onResponse(Call<ExecuteRoutineResponse> call, Response<ExecuteRoutineResponse> response) {
                    Log.d("RUN_ROUTINE", String.valueOf(response.body().result[0]));
                    result.setValue(response.body().result[0]);
                }

                @Override
                public void onFailure(Call<ExecuteRoutineResponse> call, Throwable t) {
                    result.setValue(false);
                    Log.e("RUN_ROUTINE", t.getMessage());
                }
            });
        }
        return result;
    }

    private static void verifyUpdates(Context context, List<Routine> originalData, List<Routine> newData){
        if (newData.size() > 0 && originalData.size() > 0){

            // Checking for new device additions
            if (newData.size() > originalData.size()){
                Notification.CreateCustomNotification(context, Notification.getChannelID(context, Notification.objectTypes.ROUTINE), "Nuevas Rutines", "Se cargaron nuevas rutinas, entra y miralos...", Notification.notificationIDs.NEW_ROUTINE, 2);
            } else if (newData.size() < originalData.size()){
                Notification.CreateCustomNotification(context, Notification.getChannelID(context, Notification.objectTypes.ROUTINE), "Rutinas Eliminadas", "Se eliminó una rutina, entra y ve cuál fue...", Notification.notificationIDs.DELETE_ROUTINE, 2);
            }

            RoutineCache.StoreCache(newData);
        } else {
            Log.e("VERIFY ROUTINE UPDATES", "Some collection had size 0");
        }
    }
}
