package ar.edu.itba.houseitba.ui.DispositivosTypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class AlarmConfigFragment extends DeviceConfigFragment {

    private AlarmConfigViewModel mViewModel;

    private Button btnArmStay;
    private Button btnArmAway;
    private Button btnDisarm;
    private Button btnCambiar;
    private EditText passwordET;
    private EditText newPasswordET;

    public static AlarmConfigFragment newInstance() {
        return new AlarmConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.alarm_config_fragment, container, false);

        this.btnArmStay = (Button) root.findViewById(R.id.button_arm_stay);
        this.btnArmAway = (Button) root.findViewById(R.id.button_arm_away);
        this.btnDisarm = (Button) root.findViewById(R.id.button_disarm);
        this.btnCambiar = (Button) root.findViewById(R.id.button_cambiar);
        this.passwordET = (EditText) root.findViewById(R.id.pass_input);
        this.newPasswordET = (EditText) root.findViewById(R.id.new_pass_input);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AlarmConfigViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);
        final Device device = mViewModel.getDevice();

        this.btnArmStay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordET.getText() == null){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_passwordNeeded), Toast.LENGTH_LONG).show();
                } else if (!passwordET.getText().toString().equals(device.getMeta("code"))){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_invalidPassword), Toast.LENGTH_LONG).show();
                } else {
                    MutableLiveData<Object> actionResult = null;
                    actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "armStay", passwordET.getText().toString()));
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void onChanged(Object aObject) {
                            if(!(Boolean)aObject)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_activateAlarm), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        this.btnArmAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordET.getText() == null){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_passwordNeeded), Toast.LENGTH_LONG).show();
                } else if (!passwordET.getText().toString().equals(device.getMeta("code"))){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_invalidPassword), Toast.LENGTH_LONG).show();
                } else {
                    MutableLiveData<Object> actionResult = null;
                    actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "armAway", passwordET.getText().toString()));
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void onChanged(Object aObject) {
                            if (!(Boolean) aObject)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_activateAlarm), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        this.btnDisarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordET.getText() == null){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_passwordNeeded), Toast.LENGTH_LONG).show();
                } else if (!passwordET.getText().toString().equals(device.getMeta("code"))){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_invalidPassword), Toast.LENGTH_LONG).show();
                } else {
                    MutableLiveData<Object> actionResult = null;
                    actionResult = mViewModel.doActionWithParams(new Action(device.getId(), "disarm", passwordET.getText().toString()));
                    actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                        @Override
                        public void onChanged(Object aObject) {
                            if (!(Boolean) aObject)
                                Toast.makeText(getContext(), getContext().getString(R.string.fail_deactivateAlarm), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        this.btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordET.getText() == null){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_passwordNeeded), Toast.LENGTH_LONG).show();
                } else if (!passwordET.getText().toString().equals(device.getMeta("code"))){
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_invalidPassword), Toast.LENGTH_LONG).show();
                } else {
                    if (newPasswordET.getText() == null){
                        Toast.makeText(getContext(), getContext().getString(R.string.fail_newPasswordNeeded), Toast.LENGTH_LONG).show();
                    } else if (!newPasswordET.getText().toString().matches("[0-9]{4}")){
                        Toast.makeText(getContext(), getContext().getString(R.string.fail_newPasswordInvalid), Toast.LENGTH_LONG).show();
                    } else {
                        device.changeMeta("code", newPasswordET.getText().toString());
                        mViewModel.updateDevice(device);
                        MutableLiveData<Object> actionResult = null;
                        actionResult = mViewModel.doActionWithExtendedParams(new Action(device.getId(), "changeSecurityCode", passwordET.getText().toString(), newPasswordET.getText().toString()));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if ((Boolean) aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.success_passwordChange), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_passwordChange), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

}
