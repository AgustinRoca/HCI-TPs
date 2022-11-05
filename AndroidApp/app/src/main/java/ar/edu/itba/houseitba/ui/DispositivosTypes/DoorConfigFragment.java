package ar.edu.itba.houseitba.ui.DispositivosTypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ar.edu.itba.houseitba.Classes.Action;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.R;

public class DoorConfigFragment extends DeviceConfigFragment {

    private DoorConfigViewModel mViewModel;

    private RadioGroup stateRadioGroup;
    private RadioGroup blockRadioGroup;

    public static DoorConfigFragment newInstance() {
        return new DoorConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.door_config_fragment, container, false);

        this.stateRadioGroup = (RadioGroup) root.findViewById(R.id.door_estados_radio_group);
        this.blockRadioGroup = (RadioGroup) root.findViewById(R.id.door_block_radio_group);

        return root;
    }

    private void setInputState(boolean state){
        stateRadioGroup.findViewById(R.id.radio_door_open).setEnabled(state);
        stateRadioGroup.findViewById(R.id.radio_door_close).setEnabled(state);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DoorConfigViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);
        Device device = this.mViewModel.getDevice();

        if (device.getState().status.equals("opened")){
            this.stateRadioGroup.check(R.id.radio_door_open);
        } else {
            this.stateRadioGroup.check(R.id.radio_door_close);
        }
        this.stateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                if (device.getState().lock.equals("unlocked")){
                    // Check which radio button was clicked
                    switch(checkedId) {
                        case R.id.radio_door_open:

                            actionResult = mViewModel.doAction(new Action(device.getId(), "open", null));
                            actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                                @Override
                                public void onChanged(Object aObject) {
                                    if(!(Boolean) aObject)
                                        Toast.makeText(getContext(), getContext().getString(R.string.fail_open), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        case R.id.radio_door_close:
                            actionResult = mViewModel.doAction(new Action(device.getId(), "close", null));
                            actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                                @Override
                                public void  onChanged(Object aObject) {
                                    if(!(Boolean) aObject)
                                        Toast.makeText(getContext(), getContext().getString(R.string.fail_close) , Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_openDoorFirst) , Toast.LENGTH_LONG).show();
                }
            }
        });

        if (device.getState().lock.equals("locked")){
            this.blockRadioGroup.check(R.id.radio_door_blocked);
            setInputState(false);
        } else {
            this.blockRadioGroup.check(R.id.radio_door_unblocked);
            setInputState(true);
        }
        this.blockRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_door_blocked:
                        setInputState(false);

                        actionResult = mViewModel.doAction(new Action(device.getId(), "lock", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if(!(Boolean) aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_block), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.radio_door_unblocked:
                        setInputState(true);
                        actionResult = mViewModel.doAction(new Action(device.getId(), "unlock", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void  onChanged(Object aObject) {
                                if(!(Boolean) aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_unblock) , Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });
    }

}
