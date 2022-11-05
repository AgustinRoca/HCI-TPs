package ar.edu.itba.houseitba.ui.DispositivosTypes;

import android.graphics.Color;
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

public class LampConfigFragment extends DeviceConfigFragment {

    private LampConfigViewModel mViewModel;

    private RadioGroup stateRadioGroup;
    private SeekBar brightnessSeekBar;
    private SeekBar colorSeekBar;
    private TextView progressTextView;
    private TextView colorTextView;

    public static LampConfigFragment newInstance() {
        return new LampConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.lamp_config_fragment, container, false);

        this.stateRadioGroup = (RadioGroup) root.findViewById(R.id.lamp_estados_radio_group);
        this.brightnessSeekBar = (SeekBar) root.findViewById(R.id.brightness_seekbar);
        this.colorSeekBar = (SeekBar) root.findViewById(R.id.color_seekbar);
        this.progressTextView = (TextView) root.findViewById(R.id.progress_label);
        this.colorTextView = (TextView) root.findViewById(R.id.color_show_label);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LampConfigViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);

        final Device device = this.mViewModel.getDevice();

        if (device.getState().status.equals("on")){
            this.stateRadioGroup.check(R.id.radio_lamp_on);
        } else {
            this.stateRadioGroup.check(R.id.radio_lamp_off);
        }
        this.stateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_lamp_off:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "turnOff", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if(!(Boolean) aObject)
                                    Toast.makeText(getContext(), getString(R.string.fail_off), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.radio_lamp_on:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "turnOn", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void  onChanged(Object aObject) {
                                if(!(Boolean) aObject)
                                    Toast.makeText(getContext(), getString(R.string.fail_on) , Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });

        this.brightnessSeekBar.setMax(100);
        this.brightnessSeekBar.setProgress(device.getState().brightness);
        progressTextView.setText(((Integer)device.getState().brightness).toString());

        this.brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressTextView.setText(((Integer)progress).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;
                actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setBrightness", seekBar.getProgress()));
                actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object aObject) {
                        if(aObject == null)
                            Toast.makeText(getContext(), getContext().getString(R.string.fail_brightness) , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        final float[] hsv = new float[3];
        if (device.getState().color.length() == 7){
            Color.RGBToHSV(Integer.parseInt(device.getState().color.substring(1, 3), 16),
                    Integer.parseInt(device.getState().color.substring(3, 5), 16),
                    Integer.parseInt(device.getState().color.substring(5, 7), 16),
                    hsv);
        } else {
            Color.RGBToHSV(Integer.parseInt(device.getState().color.substring(0, 2), 16),
                    Integer.parseInt(device.getState().color.substring(2, 4), 16),
                    Integer.parseInt(device.getState().color.substring(4, 6), 16),
                    hsv);
        }

        hsv[1] = 0.85f;
        hsv[2] = 0.75f;
        this.colorSeekBar.setMax(359);
        this.colorSeekBar.setProgress(((Float)hsv[0]).intValue());
        this.colorTextView.setBackgroundColor(Color.HSVToColor(hsv));

        this.colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hsv[0] = progress;
                colorTextView.setBackgroundColor(Color.HSVToColor(hsv));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                hsv[0] = seekBar.getProgress();
                int color = Color.HSVToColor(hsv);
                String hex = String.format("#%02x%02x%02x", Color.red(color), Color.green(color), Color.blue(color));
                MutableLiveData<Object> actionResult = null;
                actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "setColor", hex));
                actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object aObject) {
                        if(aObject == null)
                            Toast.makeText(getContext(), getContext().getString(R.string.fail_color) , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
