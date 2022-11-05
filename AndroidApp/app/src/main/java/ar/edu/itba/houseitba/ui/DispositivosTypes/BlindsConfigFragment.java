package ar.edu.itba.houseitba.ui.DispositivosTypes;

import android.os.Bundle;
import android.os.Handler;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import androidx.work.impl.Schedulers;
import ar.edu.itba.houseitba.Classes.Action;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.R;

public class BlindsConfigFragment extends DeviceConfigFragment {

    private BlindsConfigFragmentViewModel mViewModel;
    private RadioGroup blindsRadioGroup;
    private ProgressBar progressBar;
    private TextView progressTV;

    private Thread threadClose;
    private Thread threadOpen;
    private Handler handlerClose;
    private Handler handlerOpen;

    public static BlindsConfigFragment newInstance() {
        return new BlindsConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.blinds_config_fragment, container, false);

        this.blindsRadioGroup = (RadioGroup) root.findViewById(R.id.blinds_estados_radio_group);
        this.progressBar = (ProgressBar) root.findViewById(R.id.determinateBar);
        this.progressTV = (TextView) root.findViewById(R.id.progress_status);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mViewModel = ViewModelProviders.of(this).get(BlindsConfigFragmentViewModel.class);
        this.mViewModel.setDeviceId(this.deviceId);

        mViewModel.updateDeviceExternal().observe(getViewLifecycleOwner(), new Observer<Device>() {
            @Override
            public void onChanged(Device device) {
                if (device != null){
                    mViewModel.device = device;
                }
                updateUIToDevice();
            }
        });
    }

    private void updateUIToDevice(){
        Device device = this.mViewModel.getDevice();

        if (device.getState().status.equals("opened") || device.getState().status.equals("opening")){
            this.blindsRadioGroup.check(R.id.radio_blinds_open);
            if (device.getState().level > 0){
                progressTV.setText(getString(R.string.opening));
                progressTV.setVisibility(View.VISIBLE);
                handlerOpen = new Handler();
                threadOpen = new Thread(new Runnable() {
                    public void run() {
                        int progressStatus = progressBar.getProgress();
                        boolean isKilled = false;
                        while (progressStatus >= 0 && !isKilled) {
                            try {
                                progressStatus -= 1;
                                int finalProgressStatus = progressStatus;
                                handlerOpen.post(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(finalProgressStatus);
                                        if (progressTV.getVisibility() == View.GONE){
                                            progressTV.setVisibility(View.VISIBLE);
                                        } else{
                                            progressTV.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                isKilled = true;
                                progressTV.setVisibility(View.GONE);
                            }
                        }
                        progressTV.setVisibility(View.GONE);
                    }
                });

                if (threadClose != null){
                    threadClose.interrupt();
                }

                threadOpen.start();
            }
        } else {
            this.blindsRadioGroup.check(R.id.radio_blinds_closed);
            if (device.getState().level < 100){
                progressTV.setText(getString(R.string.closing));
                progressTV.setVisibility(View.VISIBLE);
                handlerClose = new Handler();
                threadClose = new Thread(new Runnable() {
                    public void run() {
                        int progressStatus = progressBar.getProgress();
                        boolean isKilled = false;
                        while (progressStatus <= 100 && !isKilled) {
                            try {
                                progressStatus += 1;
                                int finalProgressStatus = progressStatus;
                                handlerClose.post(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(finalProgressStatus);
                                        if (progressTV.getVisibility() == View.GONE){
                                            progressTV.setVisibility(View.VISIBLE);
                                        } else{
                                            progressTV.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                isKilled = true;
                                progressTV.setVisibility(View.GONE);
                            }
                        }
                        progressTV.setVisibility(View.GONE);
                    }
                });

                if (threadOpen != null){
                    threadOpen.interrupt();
                }

                threadClose.start();
            }
        }

        this.progressBar.setProgress(device.getState().level);

        this.blindsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final Device device = mViewModel.getDevice();
                MutableLiveData<Object> actionResult = null;

                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_blinds_closed:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "close", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if(!(Boolean)aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_close), Toast.LENGTH_LONG).show();
                                else {
                                    progressTV.setText(getString(R.string.closing));
                                    progressTV.setVisibility(View.VISIBLE);
                                    handlerClose = new Handler();
                                    threadClose = new Thread(new Runnable() {
                                        public void run() {
                                            int progressStatus = progressBar.getProgress();
                                            boolean isKilled = false;
                                            while (progressStatus <= 100 && !isKilled) {
                                                try {
                                                    progressStatus += 1;
                                                    int finalProgressStatus = progressStatus;
                                                    handlerClose.post(new Runnable() {
                                                        public void run() {
                                                            progressBar.setProgress(finalProgressStatus);
                                                            if (progressTV.getVisibility() == View.GONE){
                                                                progressTV.setVisibility(View.VISIBLE);
                                                            } else{
                                                                progressTV.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                    isKilled = true;
                                                    progressTV.setVisibility(View.GONE);
                                                }
                                            }
                                            progressTV.setVisibility(View.GONE);
                                        }
                                    });

                                    if (threadOpen != null){
                                        threadOpen.interrupt();
                                    }

                                    threadClose.start();
                                }
                            }
                        });
                        break;
                    case R.id.radio_blinds_open:
                        actionResult = mViewModel.doAction(new Action(device.getId(), "open", null));
                        actionResult.observe((LifecycleOwner) getContext(), new Observer<Object>() {
                            @Override
                            public void onChanged(Object aObject) {
                                if(!(Boolean)aObject)
                                    Toast.makeText(getContext(), getContext().getString(R.string.fail_open) , Toast.LENGTH_LONG).show();
                                else{
                                    progressTV.setText(getString(R.string.opening));
                                    progressTV.setVisibility(View.VISIBLE);
                                    handlerOpen = new Handler();
                                    threadOpen = new Thread(new Runnable() {
                                        public void run() {
                                            int progressStatus = progressBar.getProgress();
                                            boolean isKilled = false;
                                            while (progressStatus >= 0 && !isKilled) {
                                                try {
                                                    progressStatus -= 1;
                                                    int finalProgressStatus = progressStatus;
                                                    handlerOpen.post(new Runnable() {
                                                        public void run() {
                                                            progressBar.setProgress(finalProgressStatus);
                                                            if (progressTV.getVisibility() == View.GONE){
                                                                progressTV.setVisibility(View.VISIBLE);
                                                            } else{
                                                                progressTV.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                    isKilled = true;
                                                    progressTV.setVisibility(View.GONE);
                                                }
                                            }
                                            progressTV.setVisibility(View.GONE);
                                        }
                                    });

                                    if (threadClose != null){
                                        threadClose.interrupt();
                                    }

                                    threadOpen.start();
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

}
