package ar.edu.itba.houseitba.Cache;

import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.edu.itba.houseitba.Classes.Devices.Device;

public class DeviceCache {

    private static final int EXPIRATION_TIME = 30;

    private static List<Device> favDeviceList;

    private static List<Device> deviceList;

    private static HashMap<String, Device> deviceHashMap;

    private static HashMap<String, Device> favDeviceHashMap;

    private static Date lastUpdated;

    public static List<Device> GetDeviceCache(){
        if (lastUpdated == null){
            lastUpdated = new Date(1990, 1, 1);
        }

        Date aux = getCurrentTime();
        long diff = TimeUnit.MINUTES.convert((aux.getTime() - lastUpdated.getTime()), TimeUnit.MILLISECONDS);
        if (diff > EXPIRATION_TIME){
            deviceList = null;
            deviceHashMap = null;
            favDeviceList = null;
            favDeviceHashMap = null;
            return null;
        } else {
            return deviceList;
        }
    }

    public static void StoreCache(List<Device> dvcs){
        // We store the new data in the cache
        deviceList = dvcs;
        deviceHashMap = new HashMap<>();
        favDeviceHashMap = new HashMap<>();
        favDeviceList = new ArrayList<>();
        lastUpdated = getCurrentTime();

        deviceList.sort(new Comparator<Device>() {
            @Override
            public int compare(Device o1, Device o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        // We populate the fast access hash map and the favorite list
        for (Device device : deviceList){
            deviceHashMap.put(device.getId(), device);
            if (device.isFav()){
                favDeviceList.add(device);
                favDeviceHashMap.put(device.getId(), device);
            }
        }
    }

    public static Device GetDeviceById(String id){
        return deviceHashMap.get(id);
    }

    public static Device GetFavDeviceById(String id){
        return favDeviceHashMap.get(id);
    }

    public static List<Device> GetFavCache(){
        if (lastUpdated == null){
            lastUpdated = new Date(1990, 1, 1);
        }

        Date aux = getCurrentTime();
        long diff = TimeUnit.MINUTES.convert((aux.getTime() - lastUpdated.getTime()), TimeUnit.MILLISECONDS);
        if (diff > EXPIRATION_TIME){
            deviceList = null;
            deviceHashMap = null;
            favDeviceList = null;
            favDeviceHashMap = null;
            return null;
        } else {
            return favDeviceList;
        }
    }

    private static Date getCurrentTime(){
        return new Date(System.currentTimeMillis());
    }

    public static void RemoveDeviceFromFavs(Device device){
        if (favDeviceList != null){
            favDeviceList.remove(device);
            favDeviceHashMap.remove(device.getId());
        }
    }

    public static void AddDeviceToFavs(Device device){
        if (favDeviceList != null){
            favDeviceHashMap.put(device.getId(), device);
            favDeviceList.add(device);
            favDeviceList.sort(new Comparator<Device>() {
                @Override
                public int compare(Device o1, Device o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
    }

}
