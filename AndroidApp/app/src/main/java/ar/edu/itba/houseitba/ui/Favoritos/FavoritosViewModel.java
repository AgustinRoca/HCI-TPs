package ar.edu.itba.houseitba.ui.Favoritos;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Repository.DeviceRepository;

public class FavoritosViewModel extends ViewModel {
    private MutableLiveData<List<Device>> favoritos;

    public MutableLiveData<List<Device>> getFavoritos(){
        if (this.favoritos == null){
            this.favoritos = DeviceRepository.getFavoriteDevices(false);
        }
        return this.favoritos;
    }

    public MutableLiveData<List<Device>> UpdateData(){
        MutableLiveData<List<Device>> result = DeviceRepository.getFavoriteDevices(true);
        this.favoritos.setValue(result.getValue());
        return result;
    }

    public MutableLiveData<List<Device>> UpdateDataFromCache(){
        MutableLiveData<List<Device>> result = DeviceRepository.getFavoriteDevices(false);
        this.favoritos.setValue(result.getValue());
        return result;
    }

    public void SubmitData(List<Device> devices){ this.favoritos.setValue(devices); }

    public FavoritosViewModel(){
        this.favoritos = DeviceRepository.getFavoriteDevices(false);
    }

    public void RemoveDeviceFromFavs(Device device){
        DeviceRepository.removeDeviceFromFavs(device);
    }

    public void AddDeviceToFavs(Device device){
        DeviceRepository.addDeviceToFavs(device);
    }
}
