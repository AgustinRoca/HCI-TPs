package ar.edu.itba.houseitba.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.WebService.WebServiceWrapper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setTitle(getString(R.string.settings));

        final TextInputLayout ipTIL = findViewById(R.id.ip_text);
        if (ipTIL.getEditText() != null)
            ipTIL.getEditText().setText(WebServiceWrapper.getIP());
        final TextInputLayout portTIL = findViewById(R.id.port_text);
        if (portTIL.getEditText() != null)
            portTIL.getEditText().setText(WebServiceWrapper.getPort());

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean goodIP = false;
                boolean goodPort = false;
                String newPort = "";
                String newIp = "";

                if (ipTIL.getEditText() != null && ipTIL.getEditText().getText() != null && isIP(ipTIL.getEditText().getText().toString())) {
                    ipTIL.setError(null);
                    newIp = ipTIL.getEditText().getText().toString();
                    WebServiceWrapper.setIP(newIp);
                    goodIP = true;
                } else {
                    Log.e("Settings", "Not a valid IP address");
                    ipTIL.setError(getString(R.string.fail_invalid_ip));
                }

                if (portTIL.getEditText() != null && portTIL.getEditText().getText() != null && isValidPort(portTIL.getEditText().getText().toString())) {
                    portTIL.setError(null);
                    newPort = portTIL.getEditText().getText().toString();
                    WebServiceWrapper.setPort(newPort);
                    goodPort = true;
                } else {
                    Log.e("Settings", "Not a valid port");
                    portTIL.setError(getString(R.string.fail_invalid_port));
                }

                if (goodIP && goodPort){
                    WebServiceWrapper.GenerateInstance();

                    SharedPreferences prefs = getSharedPreferences(getString(R.string.app_shared_preferences_file), MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(WebServiceWrapper.SHARED_PREF_IP, newIp);
                    editor.putString(WebServiceWrapper.SHARED_PREF_PORT, newPort);
                    editor.apply();

                    Toast.makeText(view.getContext(), getString(R.string.success_APIConnection) , Toast.LENGTH_LONG).show();

                    onBackPressed();
                }
            }
        });
    }

    private boolean isIP(String ip) {
        String[] tokens = ip.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        for (String str : tokens) {
            try {
                int i = Integer.parseInt(str);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            } catch (NumberFormatException e){
                return false;
            }
        }
        return true;
    }

    private boolean isValidPort(String port){
        if(port == null)
            return false;
        try {
            int portN = Integer.parseInt(port);
            return portN >= 1024 && portN < 65536;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
