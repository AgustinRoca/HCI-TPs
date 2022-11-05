package ar.edu.itba.houseitba.ui.Favoritos;

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

import ar.edu.itba.houseitba.Adapters.FavoritosAdapter;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.R;

public class Favoritos extends Fragment {

    public static String TAG = "Favoritos_Fragment";

    private FavoritosViewModel mViewModel;
    private RecyclerView favoritosRV;
    private RecyclerView.Adapter favoritosAdapter;
    private RecyclerView.LayoutManager favoritosLayoutManager;

    public static Favoritos INSTANCE = null;

    public static Favoritos newInstance() {
        INSTANCE = new Favoritos();
        return INSTANCE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.mViewModel = ViewModelProviders.of(getActivity()).get(FavoritosViewModel.class);
        View root = inflater.inflate(R.layout.favoritos_fragment, container, false);

        setHasOptionsMenu(true);

        this.favoritosRV = (RecyclerView) root.findViewById(R.id.dispositivos_rv); // Misma vista que en dispositivos para mantener consistencia

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        this.favoritosRV.setHasFixedSize(true);
        
        int columns = 1;
        if (getActivity().getResources().getConfiguration().smallestScreenWidthDp >= 600)
            columns = 2;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns *= 2;

        this.favoritosLayoutManager = new GridLayoutManager(getContext(), columns);
        this.favoritosRV.setLayoutManager(this.favoritosLayoutManager);

        // Especificamos el adapter y le cargamos datos vacios para empezar
        this.favoritosAdapter = new FavoritosAdapter(getContext(), this.mViewModel.getFavoritos().getValue(), this.favoritosRV);
        this.favoritosRV.setAdapter(this.favoritosAdapter);


        SwipeRefreshLayout swipeRefreshContainer = (SwipeRefreshLayout) root.findViewById(R.id.favoritos_fragment);

        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                MutableLiveData<List<Device>> data = mViewModel.UpdateData();

                data.observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
                    @Override
                    public void onChanged(List<Device> devices) {
                        Log.d("FAVORITES", String.valueOf(devices));
                        ((FavoritosAdapter)favoritosAdapter).updateDataSet(devices);
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
                ((FavoritosAdapter)favoritosAdapter).updateDataSet(devices);
                favoritosAdapter.notifyDataSetChanged();
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
                    ((FavoritosAdapter)favoritosAdapter).getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    ((FavoritosAdapter)favoritosAdapter).getFilter().filter(query);
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mViewModel = ViewModelProviders.of(this).get(FavoritosViewModel.class);
        this.favoritosRV = (RecyclerView) getView().findViewById(R.id.dispositivos_rv); // Misma vista que en dispositivos para mantener consistencia

        // Create the observer which updates the UI.
        final Observer<List<Device>> dispositivosObserver = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> newData) {
                /*favoritosAdapter = new FavoritosAdapter(newData, favoritosRV);
                favoritosRV.setAdapter(favoritosAdapter);*/
                //mViewModel.SubmitData(newData);
                // Update the UI, in this case, a TextView.
                //nameTextView.setText(newName);
                if(newData !=null) {
                    ((FavoritosAdapter) favoritosAdapter).updateDataSet(newData);
                    //favoritosAdapter.notifyItemRangeChanged(0, newData.size());
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        this.mViewModel.getFavoritos().observe(getActivity(), dispositivosObserver);
    }

}
