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

public class OvenConfigFragment extends DeviceConfigFragment {

    private OvenConfigViewModel mViewModel;

    private RadioGroup stateRadioGroup;
    private RadioGroup heatRadioGroup;
    private RadioGroup grillRadioGroup;
    private RadioGroup convectionRadioGroup;
    private SeekBar temperatureSeekBar;
    private TextView progressTextView;

    public static OvenConfigFragment newInstance() {
        return new OvenConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.oven_config_fragment, container, false);

        this.stateRadioGroup = (RadioGroup) root.findViewById(R.id.oven_estados_radio_group);
        this.heatRadioGroup = (RadioGroup) root.findViewById(R.id.oven_heat_radio_group);
        this.grillRadioGroup = (RadioGroup) root.findViewById(R.id.oven_grill_radio_group);
        this.convectionRadioGroup = (RadioGroup) root.findViewById(R.id.oven_convection_radio_group);
        this.temperatureSeekBar = (SeekBar) root.findViewById(R.id.temperature_seekbar);
        this.progressTextView = (TextView) root.findViewById(R.id.progress_label);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OvenConfigViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);

        Device device = this.mViewModel.getDevice();

        if (device.getState().status.equals("on")){
            this.stateRadioGroup.check(R.id.radio_oven_on);
        } else {
            this.stateRadioGroup.check(R.id.radio_oven_off);
        }
        this.stateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_oven_off:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "turnOff", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if(!(Boolean) aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_off), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.radio_oven_on:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "turnOn", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void  onChanged(Object aObject) {
                                if(!(Boolean) aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_on) , Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });

        if (device.getState().heat.equals("conventional")){
            this.heatRadioGroup.check(R.id.radio_oven_heat_conventional);
        } else if (device.getState().heat.equals("bottom")){
            this.heatRadioGroup.check(R.id.radio_oven_heat_bottom);
        } else {
            this.heatRadioGroup.check(R.id.radio_oven_heat_top);
        }
        this.heatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_oven_heat_conventional:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHeat", "conventional"));
                        break;
                    case R.id.radio_oven_heat_bottom:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHeat", "bottom"));
                        break;
                    case R.id.radio_oven_heat_top:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setHeat", "top"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_heatMode), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        if (device.getState().grill.equals("large")){
            this.grillRadioGroup.check(R.id.radio_oven_grill_large);
        } else if (device.getState().grill.equals("eco")){
            this.grillRadioGroup.check(R.id.radio_oven_grill_eco);
        } else {
            this.grillRadioGroup.check(R.id.radio_oven_grill_off);
        }
        this.grillRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_oven_grill_large:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setGrill", "large"));
                        break;
                    case R.id.radio_oven_grill_eco:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setGrill", "eco"));
                        break;
                    case R.id.radio_oven_grill_off:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setGrill", "off"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_grillMode), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        if (device.getState().convection.equals("normal")){
            this.convectionRadioGroup.check(R.id.radio_oven_convection_normal);
        } else if (device.getState().convection.equals("eco")){
            this.convectionRadioGroup.check(R.id.radio_oven_convection_eco);
        } else {
            this.convectionRadioGroup.check(R.id.radio_oven_convection_off);
        }
        this.convectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_oven_convection_normal:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setConvection", "normal"));
                        break;
                    case R.id.radio_oven_convection_eco:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setConvection", "eco"));
                        break;
                    case R.id.radio_oven_convection_off:
                        actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setConvection", "off"));
                        break;
                }

                if (actionResult != null){
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void  onChanged(Object aObject) {
                            if (aObject == null)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_convectionMode), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });

        this.temperatureSeekBar.setMax(230 - 90);
        this.temperatureSeekBar.setProgress(device.getState().temperature - 90);
        progressTextView.setText(((Integer)device.getState().temperature).toString());

        this.temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressTextView.setText(((Integer)(progress + 90)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;
                actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setTemperature", seekBar.getProgress() + 90));
                actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object aObject) {
                        if (aObject == null)
                            Toast.makeText(getContext(), getString(R.string.fail_temperature), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
