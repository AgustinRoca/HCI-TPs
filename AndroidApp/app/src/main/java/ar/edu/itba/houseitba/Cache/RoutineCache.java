package ar.edu.itba.houseitba.Cache;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Classes.Routines.Routine;

public class RoutineCache {

    private static final int EXPIRATION_TIME = 30;

    private static List<Routine> routineList;

    private static HashMap<String, Routine> routineHashMap;

    private static Date lastUpdated;

    public static List<Routine> GetRoutineCache(){
        if (lastUpdated == null){
            lastUpdated = new Date(1990, 1, 1);
        }

        Date aux = getCurrentTime();
        long diff = TimeUnit.MINUTES.convert((aux.getTime() - lastUpdated.getTime()), TimeUnit.MILLISECONDS);
        if (diff > EXPIRATION_TIME){
            routineList = null;
            routineHashMap = null;
            return null;
        } else {
            return routineList;
        }
    }

    public static void StoreCache(List<Routine> rtns){
        // We store the new data in the cache
        routineList = rtns;
        routineHashMap = new HashMap<>();
        lastUpdated = getCurrentTime();

        routineList.sort(new Comparator<Routine>() {
            @Override
            public int compare(Routine o1, Routine o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        // We populate the fast access hash map and the favorite list
        for (Routine routine : routineList){
            routineHashMap.put(routine.getId(), routine);
        }
    }

    public static Routine GetRoutineById(String id){
        return routineHashMap.get(id);
    }

    private static Date getCurrentTime(){
        return new Date(System.currentTimeMillis());
    }
}

