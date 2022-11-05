package ar.edu.itba.houseitba.ui.Dispositivos;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Repository.DeviceRepository;

public class DispositivosViewModel extends ViewModel {
    private MutableLiveData<List<Device>> dispositivos;
    private static DispositivosViewModel INSTANCE = null;

    public MutableLiveData<List<Device>> getDispositivos(){
        if (this.dispositivos == null){
            this.dispositivos = DeviceRepository.getDevices(false);
        }
        return this.dispositivos;
    }

    public MutableLiveData<List<Device>> UpdateData(){
        MutableLiveData<List<Device>> result = DeviceRepository.getDevices(true);
        this.dispositivos.setValue(result.getValue());
        //Log.d("UPDATE_DEVICES", "DEVICES UPDATED: " + String.valueOf(result.getValue()));
        return result;
    }

    public MutableLiveData<List<Device>> UpdateDataFromCache(){
        MutableLiveData<List<Device>> result = DeviceRepository.getDevices(false);
        this.dispositivos.setValue(result.getValue());
        //Log.d("UPDATE_DEVICES", "DEVICES UPDATED: " + String.valueOf(result.getValue()));
        return result;
    }

    public void SubmitData(List<Device> devices){ this.dispositivos.setValue(devices); }

    public DispositivosViewModel(){
        this.dispositivos = DeviceRepository.getDevices(false);

        if (INSTANCE != this)
            INSTANCE = this;
    }

    public void UpdateDispositivo(Device device){
        DeviceRepository.updateDevice(device);
    }

    public void RemoveDeviceFromFavs(Device device){
        DeviceRepository.removeDeviceFromFavs(device);
    }

    public void AddDeviceToFavs(Device device){
        DeviceRepository.addDeviceToFavs(device);
    }

    public static DispositivosViewModel GetInstance(){
        return INSTANCE;
    }
}
