package ar.edu.itba.houseitba.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.houseitba.Classes.Routines.Routine;
import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.Repository.DeviceRepository;
import ar.edu.itba.houseitba.Repository.RoutineRepository;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> implements Filterable {

    private List<Routine> routines;
    private List<Routine> routinesFiltered;

    public static RoutineAdapter INSTANCE = null;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filter = constraint.toString().toLowerCase();
                if (filter.isEmpty()){
                    routinesFiltered = routines;
                } else {
                    List<Routine> filteredList = new ArrayList<>();

                    for (Routine routine : routines){
                        if (routine.getName().toLowerCase().contains(filter)){
                            filteredList.add(routine);
                        }
                    }
                    routinesFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = routinesFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                routinesFiltered = (List<Routine>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateDataset(@NonNull List<Routine> routines){
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new RoutineDiffCallback(this.routines, routines));
        this.routines = routines;
        this.routinesFiltered = routines;
        result.dispatchUpdatesTo(this);
    }

    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView actionsTextView;
        public ImageButton runButton;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.routineName_tv);
            this.actionsTextView = itemView.findViewById(R.id.routineActions_tv);
            this.runButton = itemView.findViewById(R.id.routineRun_btn);
        }
    }

    public RoutineAdapter(List<Routine> dataset){
        this.routines = dataset;
        this.routinesFiltered = routines;
        INSTANCE = this;
    }

    @NonNull
    @Override
    public RoutineAdapter.RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_card, parent, false);
        return new RoutineViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RoutineViewHolder holder, final int position) {
        holder.nameTextView.setText(this.routinesFiltered.get(position).getName());
        holder.actionsTextView.setText(holder.itemView.getContext().getString(R.string.routine_actions, ((Integer)routines.get(position).getActions().size()).toString()));
        holder.runButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                int newPosition = holder.getAdapterPosition();
                MutableLiveData<Boolean> response = RoutineRepository.runRoutine(routinesFiltered.get(newPosition).getId());
                response.observe((LifecycleOwner) view.getContext(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean) {
                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.routine_success, routines.get(newPosition).getName()), Toast.LENGTH_LONG).show();
                            DeviceRepository.getDevices(true);
                        }
                        else
                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.routine_fail, routines.get(newPosition).getName()), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if(routinesFiltered == null)
            return 0;
        return routinesFiltered.size();
    }

    class RoutineDiffCallback extends DiffUtil.Callback {

        private List<Routine> oldList;
        private List<Routine> newList;

        public RoutineDiffCallback(List<Routine> oldList, List<Routine> newList){
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            if(oldList == null)
                return 0;
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            if(newList == null)
                return 0;
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}

