package ar.edu.itba.houseitba.ui.DispositivosTypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
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

public class RefrigeratorConfigFragment extends DeviceConfigFragment {

    private RefrigeratorConfigViewModel mViewModel;

    private RadioGroup modeRadioGroup;
    private SeekBar temperatureSeekBar;
    private SeekBar temperatureFreezerSeekBar;
    private TextView temperatureTextView;
    private TextView temperatureFreezerTextView;

    public static RefrigeratorConfigFragment newInstance() {
        return new RefrigeratorConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=  inflater.inflate(R.layout.refrigerator_config_fragment, container, false);

        this.modeRadioGroup = (RadioGroup) root.findViewById(R.id.refrigerator_mode_radio_group);
        this.temperatureSeekBar = (SeekBar) root.findViewById(R.id.temperature_seekbar);
        this.temperatureTextView = (TextView) root.findViewById(R.id.progress_temperature_label);
        this.temperatureFreezerSeekBar = (SeekBar) root.findViewById(R.id.temperature_freezer_seekbar);
        this.temperatureFreezerTextView = (TextView) root.findViewById(R.id.progress_temperature_freezer_label);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RefrigeratorConfigViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);
        this.updateUIToDevice();
        this.setListeners();
    }

    private void setListeners(){
        RadioGroup.OnCheckedChangeListener listenerRadio = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_refrigerator_mode_default:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setMode", "default"));
                        break;
                    case R.id.radio_refrigerator_mode_vacation:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setMode", "vacation"));
                        break;
                    case R.id.radio_refrigerator_mode_party:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setMode", "party"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null || ((String)aObject).equals("false"))
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_mode), Toast.LENGTH_LONG).show();

                            removeListeners();

                            mViewModel.updateDeviceExternal().observe(getViewLifecycleOwner(), new Observer<Device>() {
                                @Override
                                public void onChanged(Device device) {
                                    if (device != null){
                                        mViewModel.device = device;
                                        updateUIToDevice();
                                    }
                                }
                            });

                            setListeners();
                        }
                    });
                }
            }
        };

        SeekBar.OnSeekBarChangeListener listenerTempSeekbar = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temperatureTextView.setText(((Integer)(progress + 2)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;
                actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setTemperature", seekBar.getProgress() + 2));
                actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object aObject) {
                        if (aObject == null)
                            Toast.makeText(getContext(), getContext().getString(R.string.fail_temperature), Toast.LENGTH_LONG).show();

                        removeListeners();

                        mViewModel.updateDeviceExternal().observe(getViewLifecycleOwner(), new Observer<Device>() {
                            @Override
                            public void onChanged(Device device) {
                                if (device != null){
                                    mViewModel.device = device;
                                    updateUIToDevice();
                                }
                            }
                        });

                        setListeners();
                    }
                });
            }
        };

        SeekBar.OnSeekBarChangeListener listenerFreezerTempSeekbar = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temperatureFreezerTextView.setText(((Integer)(-20 + progress)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;
                actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setFreezerTemperature", -20 + seekBar.getProgress()));
                actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object aObject) {
                        if (aObject == null)
                            Toast.makeText(getContext(), getContext().getString(R.string.fail_freezerTemp), Toast.LENGTH_LONG).show();

                        removeListeners();

                        mViewModel.updateDeviceExternal().observe(getViewLifecycleOwner(), new Observer<Device>() {
                            @Override
                            public void onChanged(Device device) {
                                if (device != null){
                                    mViewModel.device = device;
                                    updateUIToDevice();
                                }
                            }
                        });

                        setListeners();
                    }
                });
            }
        };

        this.modeRadioGroup.setOnCheckedChangeListener(listenerRadio);

        this.temperatureSeekBar.setMax(8 - 2);
        this.temperatureSeekBar.setOnSeekBarChangeListener(listenerTempSeekbar);

        this.temperatureFreezerSeekBar.setMax(12);
        this.temperatureFreezerSeekBar.setOnSeekBarChangeListener(listenerFreezerTempSeekbar);
    }

    private void removeListeners(){
        this.modeRadioGroup.setOnCheckedChangeListener(null);
        this.temperatureSeekBar.setOnSeekBarChangeListener(null);
        this.temperatureFreezerSeekBar.setOnSeekBarChangeListener(null);
    }

    private void updateUIToDevice(){
        Device device = this.mViewModel.getDevice();

        this.temperatureSeekBar.setProgress(device.getState().temperature - 2);
        this.temperatureTextView.setText(((Integer)device.getState().temperature).toString());

        this.temperatureFreezerSeekBar.setProgress(device.getState().freezerTemperature + 20);
        this.temperatureFreezerTextView.setText(((Integer)(device.getState().freezerTemperature)).toString());

        if (device.getState().mode.equals("default")){
            this.modeRadioGroup.check(R.id.radio_refrigerator_mode_default);
        } else if (device.getState().mode.equals("vacation")){
            this.modeRadioGroup.check(R.id.radio_refrigerator_mode_vacation);
        } else {
            this.modeRadioGroup.check(R.id.radio_refrigerator_mode_party);
        }
    }

}
