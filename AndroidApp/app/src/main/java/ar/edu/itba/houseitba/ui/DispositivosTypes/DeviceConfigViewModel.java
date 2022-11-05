package ar.edu.itba.houseitba.ui.DispositivosTypes;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ar.edu.itba.houseitba.Cache.DeviceCache;
import ar.edu.itba.houseitba.Classes.Action;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Repository.DeviceRepository;

public abstract class DeviceConfigViewModel extends ViewModel {
    protected Device device;

    public MutableLiveData<Object> doAction(Action action){
        return DeviceRepository.doAction(action);
    }

    public MutableLiveData<Object> doActionWithParams(Action action){
        return DeviceRepository.doActionWithParams(action);
    }

    public MutableLiveData<Object> doActionWithExtendedParams(Action action){
        return DeviceRepository.doActionWithExtendedParams(action);
    }

    public void setDeviceId(String deviceId){
        this.device = DeviceCache.GetDeviceById(deviceId);
    }

    public MutableLiveData<Device> updateDeviceExternal(){
        return DeviceRepository.getDeviceById(this.device.getId());
    }

    public Device getDevice(){
        return this.device;
    }

    public void updateDevice(Device device){
        DeviceRepository.updateDevice(device);
    }
}
