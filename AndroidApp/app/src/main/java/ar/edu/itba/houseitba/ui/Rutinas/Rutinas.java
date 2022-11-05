package ar.edu.itba.houseitba.ui.Rutinas;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import ar.edu.itba.houseitba.Adapters.RoutineAdapter;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.Classes.Routines.Routine;
import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.ui.Favoritos.Favoritos;

public class Rutinas extends Fragment {

    public static String TAG = "Rutinas_Fragment";
    private RutinasViewModel mViewModel;

    private RecyclerView routinesRV;
    private RecyclerView.Adapter routineListAdapter;
    private LinearLayoutManager routinesLayoutManager;

    public static Rutinas INSTANCE = null;

    public static Rutinas newInstance() {
        INSTANCE = new Rutinas();
        return INSTANCE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.mViewModel = ViewModelProviders.of(getActivity()).get(RutinasViewModel.class);
        View root = inflater.inflate(R.layout.rutinas_fragment, container, false);

        setHasOptionsMenu(true);

        this.routinesRV = root.findViewById(R.id.routines_rv);
        this.routinesRV.setHasFixedSize(true);

        int columns = 1;
        if (getActivity().getResources().getConfiguration().smallestScreenWidthDp >= 600)
            columns = 2;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns *= 2;

        this.routinesLayoutManager = new GridLayoutManager(getContext(), columns);

        this.routinesRV.setLayoutManager(this.routinesLayoutManager);

        this.routineListAdapter = new RoutineAdapter(this.mViewModel.getRoutines().getValue());
        this.routinesRV.setAdapter(routineListAdapter);

        SwipeRefreshLayout swipeRefreshContainer = (SwipeRefreshLayout) root.findViewById(R.id.routines_fragment);

        swipeRefreshContainer.setOnRefreshListener(() -> {

            MutableLiveData<List<Routine>> data = mViewModel.updateRoutines();

            data.observe(getViewLifecycleOwner(), new Observer<List<Routine>>() {
                @Override
                public void onChanged(List<Routine> routines) {
                    ((RoutineAdapter)routineListAdapter).updateDataset(routines);
                    swipeRefreshContainer.setRefreshing(false);
                }
            });

        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    ((RoutineAdapter)routineListAdapter).getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    ((RoutineAdapter)routineListAdapter).getFilter().filter(query);
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RutinasViewModel.class);

        final Observer<List<Routine>> routinesObserver = new Observer<List<Routine>>() {
            @Override
            public void onChanged(@Nullable final List<Routine> newData) {
                if(newData != null) {
                    routineListAdapter = new RoutineAdapter(newData);
                    routinesRV.setAdapter(routineListAdapter);
                    Log.d("UPDATE_ROUTINES", "ROUTINES UPDATED");
                }
            }
        };

        this.mViewModel.getRoutines().observe(getActivity(), routinesObserver);
    }

    public void notifyUpdateData(){
        MutableLiveData<List<Routine>> data = mViewModel.UpdateDataFromCache();

        data.observe(getViewLifecycleOwner(), new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routines) {
                ((RoutineAdapter)routineListAdapter).updateDataset(routines);
                routineListAdapter.notifyDataSetChanged();
                Log.d(TAG, "Notified data was updated");
            }
        });
    }

}
