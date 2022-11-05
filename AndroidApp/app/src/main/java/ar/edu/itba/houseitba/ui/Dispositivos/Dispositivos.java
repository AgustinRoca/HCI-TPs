package ar.edu.itba.houseitba.ui.Dispositivos;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import ar.edu.itba.houseitba.Adapters.DispositivosAdapter;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Notifications.Notification;
import ar.edu.itba.houseitba.R;

public class Dispositivos extends Fragment {

    public static String TAG = "Dispositivos_Fragment";

    private DispositivosViewModel mViewModel;
    private RecyclerView dispositivosRV;
    public RecyclerView.Adapter dispositivosAdapter;
    private RecyclerView.LayoutManager dispositivosLayoutManager;
    private SwipeRefreshLayout swipeRefreshContainer;

    public static Dispositivos INSTANCE = null;

    public static Dispositivos newInstance() {
        INSTANCE = new Dispositivos();
        return INSTANCE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.mViewModel = ViewModelProviders.of(getActivity()).get(DispositivosViewModel.class);
        View root = inflater.inflate(R.layout.dispositivos_fragment, container, false);

        INSTANCE = this;

        setHasOptionsMenu(true);

        this.dispositivosRV = (RecyclerView) root.findViewById(R.id.dispositivos_rv);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        this.dispositivosRV.setHasFixedSize(true);

        int columns = 1;
        if (getActivity().getResources().getConfiguration().smallestScreenWidthDp >= 600)
            columns = 2;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns *= 2;

        this.dispositivosLayoutManager = new GridLayoutManager(getContext(), columns);
        //((GridLayoutManager)dispositivosLayoutManager).setSpanCount(1);

        this.dispositivosRV.setLayoutManager(this.dispositivosLayoutManager);

        // Especificamos el adapter y le cargamos datos vacios para empezar
        this.dispositivosAdapter = new DispositivosAdapter(getContext(), this.mViewModel.getDispositivos().getValue());
        this.dispositivosRV.setAdapter(this.dispositivosAdapter);

        swipeRefreshContainer = (SwipeRefreshLayout) root.findViewById(R.id.dispositivos_fragment);

        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                MutableLiveData<List<Device>> data = mViewModel.UpdateData();

                data.observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
                    @Override
                    public void onChanged(List<Device> devices) {
                        ((DispositivosAdapter)dispositivosAdapter).updateDataSet(devices);
                        swipeRefreshContainer.setRefreshing(false);
                    }
                });

            }

        });

        return root;
    }

    public void notifyUpdateData(){
        MutableLiveData<List<Device>> data = mViewModel.UpdateDataFromCache();

        data.observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                ((DispositivosAdapter)dispositivosAdapter).updateDataSet(devices);
                dispositivosAdapter.notifyDataSetChanged();
                Log.d(TAG, "Notified data was updated");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    ((DispositivosAdapter)dispositivosAdapter).getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    ((DispositivosAdapter)dispositivosAdapter).getFilter().filter(query);
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mViewModel = ViewModelProviders.of(this).get(DispositivosViewModel.class);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        this.mViewModel.getDispositivos().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                Log.d("Observer", "called");
                if (devices != null){
                    ((DispositivosAdapter)dispositivosAdapter).updateDataSet(devices);
                    dispositivosAdapter.notifyDataSetChanged();
                    Log.d("UPDATE_DEVICES", "DEVICES UPDATED from observer");
                }
            }
        });
    }

}
