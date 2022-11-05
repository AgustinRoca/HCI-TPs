package ar.edu.itba.houseitba.ui.Rutinas;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Classes.Routines.Routine;
import ar.edu.itba.houseitba.Repository.DeviceRepository;
import ar.edu.itba.houseitba.Repository.RoutineRepository;

public class RutinasViewModel extends ViewModel {

    private MutableLiveData<List<Routine>> routines;

    public MutableLiveData<List<Routine>> getRoutines(){
        if (this.routines == null){
            this.routines = RoutineRepository.getRoutines(true);
        }
        return this.routines;
    }

    public MutableLiveData<List<Routine>> updateRoutines(){
        return RoutineRepository.getRoutines(true);
    }

    public MutableLiveData<Boolean> runRoutine(String id){
        return RoutineRepository.runRoutine(id);
    }

    public MutableLiveData<List<Routine>> UpdateDataFromCache(){
        MutableLiveData<List<Routine>> result = RoutineRepository.getRoutines(false);
        this.routines.setValue(result.getValue());
        return result;
    }


}
