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

public class AcConfigFragment extends DeviceConfigFragment {

    private AcConfigViewModel mViewModel;

    private RadioGroup estadosRG;
    private RadioGroup modeRG;
    private RadioGroup vsRG;
    private RadioGroup hsRG;
    private RadioGroup fsRG;

    private SeekBar temperatureSeekBar;
    private TextView progressTextView;

    public static AcConfigFragment newInstance() {
        return new AcConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ac_config_fragment, container, false);

        this.estadosRG = (RadioGroup) root.findViewById(R.id.ac_estados_radio_group);
        this.modeRG = (RadioGroup) root.findViewById(R.id.ac_mode_radio_group);
        this.vsRG = (RadioGroup) root.findViewById(R.id.ac_vs_radio_group);
        this.hsRG = (RadioGroup) root.findViewById(R.id.ac_hs_radio_group);
        this.fsRG = (RadioGroup) root.findViewById(R.id.ac_fs_radio_group);

        this.temperatureSeekBar = (SeekBar) root.findViewById(R.id.temperature_seekbar);
        this.progressTextView = (TextView) root.findViewById(R.id.progress_label);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AcConfigViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);

        Device device = this.mViewModel.getDevice();

        if (device.getState().status.equals("on")){
            this.estadosRG.check(R.id.radio_ac_on);
        } else {
            this.estadosRG.check(R.id.radio_ac_off);
        }
        this.estadosRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_ac_on:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "turnOn", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if(aObject == null)
                                    Toast.makeText(getContext(), getString(R.string.fail_on), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.radio_ac_off:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "turnOff", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void  onChanged(Object aObject) {
                                if(aObject == null)
                                    Toast.makeText(getContext(), getString(R.string.fail_off), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });

        if (device.getState().mode.equals("cool")){
            this.modeRG.check(R.id.radio_ac_mode_cool);
        } else if (device.getState().mode.equals("heat")){
            this.modeRG.check(R.id.radio_ac_mode_heat);
        } else {
            this.modeRG.check(R.id.radio_ac_mode_fan);
        }
        this.modeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_ac_mode_cool:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setMode", "cool"));
                        break;
                    case R.id.radio_ac_mode_heat:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setMode", "heat"));
                        break;
                    case R.id.radio_ac_mode_fan:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setMode", "fan"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_mode), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        if (device.getState().verticalSwing.equals("auto")){
            this.vsRG.check(R.id.radio_ac_vs_auto);
        } else if (device.getState().verticalSwing.equals("22")){
            this.vsRG.check(R.id.radio_ac_vs_22);
        } else if (device.getState().verticalSwing.equals("45")){
            this.vsRG.check(R.id.radio_ac_vs_45);
        } else if (device.getState().verticalSwing.equals("67")){
            this.vsRG.check(R.id.radio_ac_vs_67);
        } else {
            this.vsRG.check(R.id.radio_ac_vs_90);
        }
        this.vsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_ac_vs_auto:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setVerticalSwing", "auto"));
                        break;
                    case R.id.radio_ac_vs_22:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setVerticalSwing", "22"));
                        break;
                    case R.id.radio_ac_vs_45:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setVerticalSwing", "45"));
                        break;
                    case R.id.radio_ac_vs_67:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setVerticalSwing", "67"));
                        break;
                    case R.id.radio_ac_vs_90:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setVerticalSwing", "90"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), progressTextView.getContext().getString(R.string.fail_verticalSwing), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        if (device.getState().horizontalSwing.equals("auto")){
            this.hsRG.check(R.id.radio_ac_hs_auto);
        } else if (device.getState().horizontalSwing.equals("-90")){
            this.hsRG.check(R.id.radio_ac_hs_m90);
        } else if (device.getState().horizontalSwing.equals("-45")){
            this.hsRG.check(R.id.radio_ac_hs_m45);
        } else if (device.getState().horizontalSwing.equals("0")){
            this.hsRG.check(R.id.radio_ac_hs_0);
        } else if (device.getState().horizontalSwing.equals("45")){
            this.hsRG.check(R.id.radio_ac_hs_45);
        } else {
            this.hsRG.check(R.id.radio_ac_hs_90);
        }
        this.hsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_ac_hs_auto:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHorizontalSwing", "auto"));
                        break;
                    case R.id.radio_ac_hs_m90:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHorizontalSwing", "-90"));
                        break;
                    case R.id.radio_ac_hs_m45:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHorizontalSwing", "-45"));
                        break;
                    case R.id.radio_ac_hs_0:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHorizontalSwing", "0"));
                        break;
                    case R.id.radio_ac_hs_45:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHorizontalSwing", "45"));
                        break;
                    case R.id.radio_ac_hs_90:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHorizontalSwing", "90"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_horizontalSwing), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });

        if (device.getState().fanSpeed.equals("auto")){
            this.fsRG.check(R.id.radio_ac_fs_auto);
        } else if (device.getState().fanSpeed.equals("25")){
            this.fsRG.check(R.id.radio_ac_fs_25);
        } else if (device.getState().fanSpeed.equals("50")){
            this.fsRG.check(R.id.radio_ac_fs_50);
        } else if (device.getState().fanSpeed.equals("75")){
            this.fsRG.check(R.id.radio_ac_fs_75);
        } else {
            this.fsRG.check(R.id.radio_ac_fs_100);
        }
        this.fsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_ac_fs_auto:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setFanSpeed", "auto"));
                        break;
                    case R.id.radio_ac_fs_25:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setFanSpeed", "25"));
                        break;
                    case R.id.radio_ac_fs_50:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setFanSpeed", "50"));
                        break;
                    case R.id.radio_ac_fs_75:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setFanSpeed", "75"));
                        break;
                    case R.id.radio_ac_fs_100:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setFanSpeed", "100"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_fanSpeed), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });

        this.temperatureSeekBar.setMax(38 - 18);
        this.temperatureSeekBar.setProgress(device.getState().temperature - 18);
        progressTextView.setText(((Integer)device.getState().temperature).toString());

        this.temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressTextView.setText(((Integer)(progress + 18)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;
                actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setTemperature", seekBar.getProgress() + 18));
                actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object aObject) {
                        if (aObject == null)
                            Toast.makeText(getContext(), getContext().getString(R.string.fail_temperature), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
