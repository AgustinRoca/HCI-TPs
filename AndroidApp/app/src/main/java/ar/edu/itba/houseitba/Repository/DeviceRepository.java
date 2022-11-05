package ar.edu.itba.houseitba.Repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ar.edu.itba.houseitba.Activities.DeviceActivity;
import ar.edu.itba.houseitba.Activities.MainActivity;
import ar.edu.itba.houseitba.Cache.DeviceCache;
import ar.edu.itba.houseitba.Classes.Action;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Classes.Devices.DeviceResponse;
import ar.edu.itba.houseitba.Classes.Devices.OneDeviceResponse;
import ar.edu.itba.houseitba.Notifications.Notification;
import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.WebService.WebServiceWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ar.edu.itba.houseitba.Utils.Constants.AC;
import static ar.edu.itba.houseitba.Utils.Constants.ALARM;
import static ar.edu.itba.houseitba.Utils.Constants.BLINDS;
import static ar.edu.itba.houseitba.Utils.Constants.DOOR;
import static ar.edu.itba.houseitba.Utils.Constants.LAMP;
import static ar.edu.itba.houseitba.Utils.Constants.OVEN;
import static ar.edu.itba.houseitba.Utils.Constants.REFRIGERATOR;

public class DeviceRepository extends Repository {

    public static MutableLiveData<List<Device>> getDevices(boolean forcedUpdate){
        final MutableLiveData<List<Device>> data = new MutableLiveData<>();
        List<Device> cache = DeviceCache.GetDeviceCache();
        if (cache == null || forcedUpdate){
            if (WebServiceWrapper.GetServiceInstance() != null){
                WebServiceWrapper.GetServiceInstance().getDevices().enqueue(new Callback<DeviceResponse>() {
                    @Override
                    public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                        if (response.body() != null){
                            List<Device> aux = response.body().devices;
                            aux.sort(new Comparator<Device>() {
                                @Override
                                public int compare(Device o1, Device o2) {
                                    return o1.getName().compareTo(o2.getName());
                                }
                            });
                            data.setValue(aux);
                            DeviceCache.StoreCache(aux);
                        } else {
                            Log.e("GET DEVICES", "NULL BODY");
                        }
                    }

                    @Override
                    public void onFailure(Call<DeviceResponse> call, Throwable t) {
                        data.postValue(new ArrayList<Device>());
                        Log.e("GET DEVICES", t.getMessage());
                    }
                });
            }
        } else {
            data.setValue(cache);
        }

        return data;
    }

    public static MutableLiveData<Device> getDeviceById(String id){
        final MutableLiveData<Device> data = new MutableLiveData<>();
        if (WebServiceWrapper.GetServiceInstance() != null){
            WebServiceWrapper.GetServiceInstance().getDeviceById(id).enqueue(new Callback<OneDeviceResponse>() {
                @Override
                public void onResponse(Call<OneDeviceResponse> call, Response<OneDeviceResponse> response) {
                    if (response.body() != null){
                        Device aux = response.body().device;
                        data.setValue(aux);
                    } else {
                        Log.e("GET DEVICE BY ID", "NULL BODY");
                    }
                }

                @Override
                public void onFailure(Call<OneDeviceResponse> call, Throwable t) {
                    data.postValue(null);
                    Log.e("GET DEVICE BY ID", t.getMessage());
                }
            });
        }

        return data;
    }


    public static MutableLiveData<List<Device>> getFavoriteDevices(boolean force){
        final MutableLiveData<List<Device>> data = new MutableLiveData<>();
        List<Device> cache = DeviceCache.GetFavCache();
        if (cache == null || force) {
            if (WebServiceWrapper.GetServiceInstance() != null) {
                WebServiceWrapper.GetServiceInstance().getDevices().enqueue(new Callback<DeviceResponse>() {
                    @Override
                    public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                        if (response.body() != null){
                            List<Device> aux = response.body().devices;
                            aux.sort(new Comparator<Device>() {
                                @Override
                                public int compare(Device o1, Device o2) {
                                    return o1.getName().compareTo(o2.getName());
                                }
                            });
                            List<Device> favorites = new ArrayList<>();
                            for (Device device : aux) {
                                if (Boolean.parseBoolean(device.getMeta("fav"))) {
                                    favorites.add(device);
                                }
                            }
                            DeviceCache.StoreCache(aux);
                            data.setValue(favorites);
                        } else {
                            Log.e("GET DEVICES", "NULL BODY");
                        }
                    }

                    @Override
                    public void onFailure(Call<DeviceResponse> call, Throwable t) {
                        data.postValue(new ArrayList<Device>());
                        Log.e("GET FAVORITES", t.getMessage());
                    }
                });
            }
        } else {
            data.setValue(cache);
        }

        return data;
    }

    public static void updateDevice(Device device){
        if (WebServiceWrapper.GetServiceInstance() != null){
            List<Device> cache = DeviceCache.GetDeviceCache();

            if (cache != null){
                cache.remove(device);
                cache.add(device);
                DeviceCache.StoreCache(cache);
            }

            WebServiceWrapper.GetServiceInstance().updateDevice(device.getId(), device.getBodyForUpdate()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.i("UPDATE DEVICES", "SUCCESS");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("UPDATE DEVICES", t.getMessage());
                }
            });
        }
    }

    public static void removeDeviceFromFavs(Device device){
        DeviceCache.RemoveDeviceFromFavs(device);
    }

    public static void addDeviceToFavs(Device device){
        DeviceCache.AddDeviceToFavs(device);
    }

    public static MutableLiveData<Object> doAction(Action action){
        final MutableLiveData<Object> result = new MutableLiveData<>();
        WebServiceWrapper.GetServiceInstance().doAction(action.deviceId, action.actionName).enqueue(new Callback<ar.edu.itba.houseitba.Classes.Response>() {
            @Override
            public void onResponse(Call<ar.edu.itba.houseitba.Classes.Response> call, Response<ar.edu.itba.houseitba.Classes.Response> response) {
                if (response.body() != null){
                    result.setValue(response.body().result);
                    getDevices(true);
                }
                else{
                    result.setValue(false);
                    Log.e("DO_ACTION", "NULL BODY");
                }
            }

            @Override
            public void onFailure(Call<ar.edu.itba.houseitba.Classes.Response> call, Throwable t) {
                result.setValue(false);
                Log.e("DO_ACTION", t.getMessage());
            }
        });
        return result;
    }

    public static MutableLiveData<Object> doActionWithParams(Action action){
        final MutableLiveData<Object> result = new MutableLiveData<>();
        WebServiceWrapper.GetServiceInstance().doActionWithParams(action.deviceId, action.actionName, action.getBody()).enqueue(new Callback<ar.edu.itba.houseitba.Classes.Response>() {
            @Override
            public void onResponse(Call<ar.edu.itba.houseitba.Classes.Response> call, Response<ar.edu.itba.houseitba.Classes.Response> response) {
                if (response.body() != null){
                    result.setValue(response.body().result);
                    getDevices(true);
                }
                else{
                    result.setValue(false);
                    Log.e("DO_ACTION", "NULL BODY");
                }
            }

            @Override
            public void onFailure(Call<ar.edu.itba.houseitba.Classes.Response> call, Throwable t) {
                result.setValue(false);
                Log.e("DO_ACTION", t.getMessage());
            }
        });
        return result;
    }

    public static MutableLiveData<Object> doActionWithExtendedParams(Action action){
        final MutableLiveData<Object> result = new MutableLiveData<>();
        WebServiceWrapper.GetServiceInstance().doActionWithExtendedParams(action.deviceId, action.actionName, action.getExtendedBody()).enqueue(new Callback<ar.edu.itba.houseitba.Classes.Response>() {
            @Override
            public void onResponse(Call<ar.edu.itba.houseitba.Classes.Response> call, Response<ar.edu.itba.houseitba.Classes.Response> response) {
                if (response.body() != null){
                    result.setValue(response.body().result);
                    getDevices(true);
                }
                else{
                    result.setValue(false);
                    Log.e("DO_ACTION", "NULL BODY");
                }
            }

            @Override
            public void onFailure(Call<ar.edu.itba.houseitba.Classes.Response> call, Throwable t) {
                result.setValue(false);
                Log.e("DO_ACTION", t.getMessage());
            }
        });
        return result;
    }

    public static void getDevicesByWorker(final LifecycleOwner lco, final Context context){
        if (WebServiceWrapper.GetServiceInstance() != null){
            WebServiceWrapper.GetServiceInstance().getDevices().enqueue(new Callback<DeviceResponse>() {
                @Override
                public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                    if (response.body() != null){
                        if (DeviceCache.GetDeviceCache() != null){
                            verifyUpdates(context, DeviceCache.GetDeviceCache(), response.body().devices);
                        }
                        DeviceCache.StoreCache(response.body().devices);
                        MainActivity.getInstance().notifyWorkerFinished();
                    }

                    //Notification.CreateNotification(context);

                    //data.setValue(response.body().devices);
                    //DeviceCache.StoreCache(response.body().devices);
                }

                @Override
                public void onFailure(Call<DeviceResponse> call, Throwable t) {
                    Log.e("GET DEVICES", t.getMessage());
                }
            });
        }
    }

    private static void verifyUpdates(Context context, List<Device> originalData, List<Device> newData){
        if (newData.size() > 0 && originalData.size() > 0){

            // Checking for new device additions
            if (newData.size() > originalData.size()){
                Notification.CreateCustomNotification(context, Notification.getChannelID(context, Notification.objectTypes.DEVICE), context.getString(R.string.notif_newDevice_title), context.getString(R.string.notif_newDevice_body), Notification.notificationIDs.NEW_DEVICE, 1);
            } else if (newData.size() < originalData.size()){
                Notification.CreateCustomNotification(context, Notification.getChannelID(context, Notification.objectTypes.DEVICE), context.getString(R.string.notf_deviceDeleted_title), context.getString(R.string.notif_deviceDeleted_body), Notification.notificationIDs.DELETE_DEVICE, 1);
            }

            // Iterating through the new list of devices
            for(Device newDevice : newData){

                Device oldDevice = DeviceCache.GetDeviceById(newDevice.getId());

                if (oldDevice == null){
                    if (newDevice.isFav()){
                        Notification.CreateCustomNotification(context, Notification.getChannelID(context, Notification.objectTypes.DEVICE), context.getString(R.string.notif_newDevice_title), context.getString(R.string.notif_newDevice_body), Notification.notificationIDs.NEW_FAV, 0);
                    }
                }

                // Checking if the old device is a favorite
                if(oldDevice != null && oldDevice.isFav()){

                    String deviceType = oldDevice.getTypeId();
                    Device.DeviceState oldDeviceState = oldDevice.getState();
                    Device.DeviceState newDeviceState = newDevice.getState();

                    if(oldDeviceState != null && newDeviceState != null){
                        switch(deviceType){
                            case BLINDS:
                                if(!oldDeviceState.status.equals(newDeviceState.status)){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }
                                break;
                            case LAMP:
                                if(!oldDeviceState.status.equals(newDeviceState.status) || !oldDeviceState.color.equals(newDeviceState.color) || oldDeviceState.brightness != newDeviceState.brightness){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }
                                break;
                            case OVEN:
                                if(!oldDeviceState.status.equals(newDeviceState.status) || oldDeviceState.temperature != newDeviceState.temperature || !oldDeviceState.heat.equals(newDeviceState.heat) || !oldDeviceState.grill.equals(newDeviceState.grill) || !oldDeviceState.convection.equals(newDeviceState.convection)){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }
                                break;
                            case AC:
                                if(!oldDeviceState.status.equals(newDeviceState.status) || oldDeviceState.temperature != newDeviceState.temperature || !oldDeviceState.mode.equals(newDeviceState.mode) || !oldDeviceState.verticalSwing.equals(newDeviceState.verticalSwing) || !oldDeviceState.horizontalSwing.equals(newDeviceState.horizontalSwing) || !oldDeviceState.fanSpeed.equals(newDeviceState.fanSpeed)){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }
                                break;
                            case DOOR:
                                if(!oldDeviceState.status.equals(newDeviceState.status) || !oldDeviceState.lock.equals(newDeviceState.lock) ){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }
                                break;
                            case ALARM:
                                if(!oldDeviceState.status.equals(newDeviceState.status)){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }
                                break;
                            case REFRIGERATOR:
                                if(oldDeviceState.temperature != newDeviceState.temperature || oldDeviceState.freezerTemperature != newDeviceState.freezerTemperature || !oldDeviceState.mode.equals(newDeviceState.mode)){
                                    updateNotification(context, oldDevice.getName(), newDevice);
                                }

                                break;
                        }
                    }
                }
                else{
                    if (oldDevice == null)
                        Log.i("VERIFY DEVICE UPDATES", "Nuewvo dispositivo");
                    else
                        Log.i("VERIFY DEVICE UPDATES", "Dispositivo no favorito");
                }

            }
        } else {
            Log.e("VERIFY DEVICE UPDATES", "Some collection had size 0");
        }
    }

    public static void updateNotification(Context context, String deviceName, Device device){
        Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra(MainActivity.DEVICE_ID_EXTRA, device.getId());
        intent.putExtra(MainActivity.DEVICE_NAME_EXTRA, device.getName());
        intent.putExtra(MainActivity.DEVICE_TYPE_EXTRA, device.getTypeId());
        Notification.CreateCustomNotification(context, Notification.getChannelID(context, Notification.objectTypes.DEVICE),context.getString(R.string.notif_deviceChanged_title), context.getString(R.string.notif_deviceChanged_body, deviceName),Notification.notificationIDs.DEVICE_STATE_CHANGE, intent);
    }
}
