package ar.edu.itba.houseitba.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.edu.itba.houseitba.Adapters.DispositivosAdapter;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Notifications.Notification;
import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.WebService.WebServiceWrapper;
import ar.edu.itba.houseitba.Workers.UpdateWorker;
import ar.edu.itba.houseitba.ui.Dispositivos.Dispositivos;
import ar.edu.itba.houseitba.ui.Dispositivos.DispositivosViewModel;
import ar.edu.itba.houseitba.ui.Favoritos.Favoritos;
import ar.edu.itba.houseitba.ui.Rutinas.Rutinas;

public class MainActivity extends AppCompatActivity {

    public static final String DEVICE_ID_EXTRA = "device_id_extra";
    public static final String DEVICE_NAME_EXTRA = "device_name_extra";
    public static final String DEVICE_TYPE_EXTRA = "device_type_extra";

    private static MainActivity Instance = null;
    private NavController navController;

    private RecyclerView.Adapter dispositivosAdapter;
    private DispositivosViewModel dispositivosViewModel;

    private SearchView searchView;

    private boolean hasRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Instance != this){
            Instance = this;
        }

        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_shared_preferences_file), MODE_PRIVATE);
        String ip = prefs.getString(WebServiceWrapper.SHARED_PREF_IP, null);
        String port = prefs.getString(WebServiceWrapper.SHARED_PREF_PORT, null);

        if (ip != null && port != null){
            WebServiceWrapper.setIP(ip);
            WebServiceWrapper.setPort(port);
            WebServiceWrapper.GenerateInstance();
        }

        //WebServiceWrapper.GenerateInstance();
        if (WebServiceWrapper.GetServiceInstance() == null)
            Toast.makeText(this, getString(R.string.alert_connectApi) , Toast.LENGTH_LONG).show();
        else {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED )
                    .build();
            

            PeriodicWorkRequest updateRequest =
                    new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .setInitialDelay(30, TimeUnit.SECONDS)
                            .build();

            WorkManager.getInstance(this)
                    .enqueueUniquePeriodicWork("UpdateWorker", ExistingPeriodicWorkPolicy.KEEP, updateRequest);


            WorkManager.getInstance(this).getWorkInfoByIdLiveData(updateRequest.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {
                            if (workInfo != null){
                                switch (workInfo.getState()){
                                    case RUNNING:
                                        hasRun = true;
                                        break;
                                    case ENQUEUED:
                                        if (hasRun){
                                            dispositivosViewModel.UpdateData();
                                            hasRun = false;
                                        }
                                        break;
                                }
                            }
                        }
                    });
        }
        // -----------------------------------------------------------------------------------------

        // Setting up bottom navigation
        // -----------------------------------------------------------------------------------------

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.favoritos, R.id.dispositivos,  R.id.rutinas)
                .build();
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, this.navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, this.navController);
        int id = R.id.dispositivos;
        switch (getIntent().getIntExtra("navState" ,1)){
            case 0:
                id = R.id.favoritos;
                break;
            case 1:
                id = R.id.dispositivos;
                break;
            case 2:
                id = R.id.rutinas;
                break;
        }
        navController.navigate(id);

        //dispositivosViewModel = ViewModelProviders.of(this).get(DispositivosViewModel.class);

        // -----------------------------------------------------------------------------------------

        // Especificamos el adapter y le cargamos datos vacios para empezar
        this.dispositivosAdapter = new DispositivosAdapter(this, new ArrayList<Device>());

        dispositivosViewModel = ViewModelProviders.of(this).get(DispositivosViewModel.class);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        dispositivosViewModel.getDispositivos().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                Dispositivos dispositivos = (Dispositivos) getSupportFragmentManager().findFragmentById(R.id.dispositivos_fragment);
                if (dispositivos != null){
                    ((DispositivosAdapter)dispositivos.dispositivosAdapter).SubmitData(devices);
                    ((DispositivosAdapter)dispositivos.dispositivosAdapter).notifyItemRangeChanged(0, devices.size());
                    ((DispositivosAdapter)dispositivos.dispositivosAdapter).notifyDataSetChanged();
                }
                //((DispositivosAdapter) dispositivosAdapter).SubmitData(devices);

            }
        });

        // Setting up work updates
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------

        // Setting up notifications
        // -----------------------------------------------------------------------------------------

        this.createNotificationChannel(
                Notification.getChannelID(this, Notification.objectTypes.DEVICE),
                Notification.getChannelName(this, Notification.objectTypes.DEVICE),
                Notification.getChannelDescription(this, Notification.objectTypes.DEVICE)
        );
        this.createNotificationChannel(
                Notification.getChannelID(this, Notification.objectTypes.ROUTINE),
                Notification.getChannelName(this, Notification.objectTypes.ROUTINE),
                Notification.getChannelDescription(this, Notification.objectTypes.ROUTINE)
        );

        // Setting up notifications
        // -----------------------------------------------------------------------------------------
    }

    public void notifyWorkerFinished(){
        if (Dispositivos.INSTANCE != null)
            Dispositivos.INSTANCE.notifyUpdateData();
        if (Favoritos.INSTANCE != null)
            Favoritos.INSTANCE.notifyUpdateData();
        if (Rutinas.INSTANCE != null)
            Rutinas.INSTANCE.notifyUpdateData();
    }

    public static MainActivity getInstance(){
        return Instance;
    }

//    public static DispositivosViewModel getDispositivosViewModelInstance(){
//        return dispositivosViewModel;
//    }

    public void createNotificationChannel(String id, String name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings_opt:
                Intent intent = new Intent(MainActivity.getInstance(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
