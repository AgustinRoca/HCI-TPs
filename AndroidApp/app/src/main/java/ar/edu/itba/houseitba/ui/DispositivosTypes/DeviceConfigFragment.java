package ar.edu.itba.houseitba.ui.DispositivosTypes;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

/**
 * Abstract class to add methods to the multiple device types supported
 * Simplifies the code for fragment selection
 * Extends from the Fragment because we cant have polymorphism, so each device fragment must extend this class
 */
public abstract class DeviceConfigFragment extends Fragment {
    public String deviceId = null;

    public void setData(String deviceId){
        this.deviceId = deviceId;
    }
}
