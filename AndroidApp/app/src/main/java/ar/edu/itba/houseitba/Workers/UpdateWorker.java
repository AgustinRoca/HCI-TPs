package ar.edu.itba.houseitba.Workers;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ar.edu.itba.houseitba.Activities.MainActivity;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Notifications.Notification;
import ar.edu.itba.houseitba.Repository.DeviceRepository;
import ar.edu.itba.houseitba.Repository.RoutineRepository;
import ar.edu.itba.houseitba.ui.Dispositivos.DispositivosViewModel;

public class UpdateWorker extends Worker {

    public UpdateWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Log.d("WORKER", "doWorkStarted");

        DeviceRepository.getDevicesByWorker(MainActivity.getInstance(), getApplicationContext());
        RoutineRepository.getRoutinesByWorker(MainActivity.getInstance(), getApplicationContext());

        //DispositivosViewModel.GetInstance().UpdateData();

        Log.d("WORKER", "doWorkEnded");

        return Result.success();
    }
}
