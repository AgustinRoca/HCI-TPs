package ar.edu.itba.houseitba.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.ui.DispositivosTypes.AcConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.AlarmConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.BlindsConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.DeviceConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.DoorConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.LampConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.OvenConfigFragment;
import ar.edu.itba.houseitba.ui.DispositivosTypes.RefrigeratorConfigFragment;

import static ar.edu.itba.houseitba.Utils.Constants.AC;
import static ar.edu.itba.houseitba.Utils.Constants.ALARM;
import static ar.edu.itba.houseitba.Utils.Constants.BLINDS;
import static ar.edu.itba.houseitba.Utils.Constants.DOOR;
import static ar.edu.itba.houseitba.Utils.Constants.LAMP;
import static ar.edu.itba.houseitba.Utils.Constants.OVEN;
import static ar.edu.itba.houseitba.Utils.Constants.REFRIGERATOR;

public class DeviceActivity extends AppCompatActivity {

    private NavController navController;
    private DeviceConfigFragment deviceConfigFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String deviceId = intent.getStringExtra(MainActivity.DEVICE_ID_EXTRA);
        String deviceName = intent.getStringExtra(MainActivity.DEVICE_NAME_EXTRA);
        String deviceType = intent.getStringExtra(MainActivity.DEVICE_TYPE_EXTRA);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setTitle(deviceName);

        // This switch determines the device type fragment to be loaded
        // Generates a new instance of that class
        switch(deviceType){
            case BLINDS:
                this.deviceConfigFragment = BlindsConfigFragment.newInstance();
                break;
            case LAMP:
                this.deviceConfigFragment = LampConfigFragment.newInstance();
                break;
            case OVEN:
                this.deviceConfigFragment = OvenConfigFragment.newInstance();
                break;
            case AC:
                this.deviceConfigFragment = AcConfigFragment.newInstance();
                break;
            case DOOR:
                this.deviceConfigFragment = DoorConfigFragment.newInstance();
                break;
            case ALARM:
                this.deviceConfigFragment = AlarmConfigFragment.newInstance();
                break;
            case REFRIGERATOR:
                this.deviceConfigFragment = RefrigeratorConfigFragment.newInstance();
                break;
        }

        deviceConfigFragment.setData(deviceId);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.device_container, deviceConfigFragment)
                    .commitNow();
        }
    }
}
