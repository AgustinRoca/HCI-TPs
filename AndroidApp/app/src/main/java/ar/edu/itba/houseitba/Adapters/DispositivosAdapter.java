package ar.edu.itba.houseitba.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.houseitba.Activities.DeviceActivity;
import ar.edu.itba.houseitba.Activities.MainActivity;
import ar.edu.itba.houseitba.Classes.Devices.Device;
import ar.edu.itba.houseitba.R;
import ar.edu.itba.houseitba.Repository.DeviceRepository;
import ar.edu.itba.houseitba.Utils.Constants;
import ar.edu.itba.houseitba.Utils.Util;

public class DispositivosAdapter extends RecyclerView.Adapter<DispositivosAdapter.DispositivosViewHolder> implements Filterable {
    private List<Device> dataset;
    private List<Device> datasetFiltered;

    private Context mContext;

    public static DispositivosAdapter INSTANCE = null;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filter = constraint.toString().toLowerCase();
                String deviceType;
                if (filter.isEmpty()){
                    datasetFiltered = dataset;
                } else {
                    List<Device> filteredList = new ArrayList<>();

                    for (Device dev : dataset){
                        deviceType = mContext.getResources().getString(Util.getResId(dev.getTypeName(), R.string.class));
                        if (dev.getName().toLowerCase().contains(filter) || deviceType.toLowerCase().contains(filter)){
                            filteredList.add(dev);
                        }
                    }
                    datasetFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = datasetFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datasetFiltered = (List<Device>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateDataSet(List<Device> newData){
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DeviceDiffCallback(dataset, newData));
        this.dataset = newData;
        this.datasetFiltered = newData;
        result.dispatchUpdatesTo(this);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DispositivosViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView primaryTextView;
        public TextView secondaryTextView;
        //public TextView secondaryStatusTextView;
        public ToggleButton favorite;
        public DispositivosViewHolder(@NotNull View v) {
            super(v);
            primaryTextView = v.findViewById(R.id.primary_text_view_id);
            secondaryTextView = v.findViewById(R.id.secondary_text_view_id);
            //secondaryStatusTextView = v.findViewById(R.id.secondary_text_view_status_id);
            favorite = v.findViewById(R.id.button_favorite_id);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DispositivosAdapter(Context context, List<Device> dataset) {
        this.dataset = dataset;
        this.mContext = context;
        this.datasetFiltered = this.dataset;
        INSTANCE = this;
    }

    public void SubmitData(List<Device> dataset){
        this.dataset = dataset;
        this.datasetFiltered = this.dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DispositivosAdapter.DispositivosViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dispositivo_layout, parent, false);

        DispositivosViewHolder vh = new DispositivosViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DispositivosViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.favorite.setOnCheckedChangeListener(null);
        holder.primaryTextView.setText(this.datasetFiltered.get(position).getName());
        holder.secondaryTextView.setText(mContext.getResources().getString(Util.getResId(this.datasetFiltered.get(position).getTypeName(), R.string.class)));
        //holder.secondaryStatusTextView.setText(getDeviceStatus(this.datasetFiltered.get(position)));
        holder.favorite.setChecked(this.datasetFiltered.get(position).isFav());
        holder.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int newPosition = holder.getAdapterPosition();
                datasetFiltered.get(newPosition).addMeta("fav", Boolean.toString(b));
                if (b){
                    DeviceRepository.addDeviceToFavs(datasetFiltered.get(newPosition));
                } else {
                    DeviceRepository.removeDeviceFromFavs(datasetFiltered.get(newPosition));
                }
                DeviceRepository.updateDevice(datasetFiltered.get(newPosition));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = holder.getAdapterPosition();
                Intent intent = new Intent(MainActivity.getInstance(), DeviceActivity.class);
                intent.putExtra(MainActivity.DEVICE_ID_EXTRA, datasetFiltered.get(newPosition).getId());
                intent.putExtra(MainActivity.DEVICE_NAME_EXTRA, datasetFiltered.get(newPosition).getName());
                intent.putExtra(MainActivity.DEVICE_TYPE_EXTRA, datasetFiltered.get(newPosition).getTypeId());
                MainActivity.getInstance().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (this.dataset == null)
            return 0;
        return this.datasetFiltered.size();
    }

    public static class DeviceDiffCallback extends DiffUtil.Callback{


        private List<Device> oldList;
        private List<Device> newList;

        public DeviceDiffCallback(List<Device> oldList, List<Device> newList){
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
            return oldList.get(oldItemPosition).getMeta("fav").equals(newList.get(newItemPosition).getMeta("fav"));
        }
    }
}
