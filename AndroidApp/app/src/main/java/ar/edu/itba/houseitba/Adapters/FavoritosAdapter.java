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

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.FavoritosViewHolder> implements Filterable {
    private List<Device> dataset;
    private List<Device> datasetFiltered;
    private RecyclerView parent;

    public static FavoritosAdapter INSTANCE = null;

    private Context mContext;

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
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DispositivosAdapter.DeviceDiffCallback(dataset, newData));
        this.dataset = newData;
        this.datasetFiltered = newData;
        result.dispatchUpdatesTo(this);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FavoritosViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView primaryTextView;
        public TextView secondaryTextView;
        //public TextView secondaryStatusTextView;
        public ToggleButton favorite;
        public FavoritosViewHolder(@NotNull View v) {
            super(v);
            primaryTextView = v.findViewById(R.id.primary_text_view_id);
            secondaryTextView = v.findViewById(R.id.secondary_text_view_id);
            //secondaryStatusTextView = v.findViewById(R.id.secondary_text_view_status_id);
            favorite = v.findViewById(R.id.button_favorite_id);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FavoritosAdapter(Context context, List<Device> dataset, RecyclerView rv) {
        this.dataset = dataset;
        this.datasetFiltered = this.dataset;
        this.mContext = context;
        this.parent = rv;
        INSTANCE = this;
    }

    public void SubmitData(List<Device> dataset){
        this.dataset = dataset;
        this.datasetFiltered = this.dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavoritosAdapter.FavoritosViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dispositivo_layout, parent, false);

        FavoritosAdapter.FavoritosViewHolder vh = new FavoritosAdapter.FavoritosViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FavoritosAdapter.FavoritosViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.favorite.setOnCheckedChangeListener(null);
        holder.primaryTextView.setText(this.datasetFiltered.get(position).getName());
        holder.secondaryTextView.setText(parent.getContext().getResources().getString(Util.getResId(this.datasetFiltered.get(position).getTypeName(), R.string.class)));
        //holder.secondaryStatusTextView.setText(getDeviceStatus(this.datasetFiltered.get(position)));
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
        holder.favorite.setChecked(Boolean.parseBoolean(this.datasetFiltered.get(position).getMeta("fav")));
        holder.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                int newPosition = holder.getAdapterPosition();
                Device device = datasetFiltered.get(newPosition);
                datasetFiltered.remove(newPosition);
                notifyItemRemoved(newPosition);

                device.changeMeta("fav", Boolean.toString(b));
                dataset.remove(device);
                DeviceRepository.updateDevice(device);
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

    private String getDeviceStatus(Device device){
        String type = device.getTypeId();
        int resID = 0;
        switch(type){
            case Constants.LAMP:
            case Constants.OVEN:
            case Constants.AC:
                if (device.getState().status.equals("on"))
                    resID = R.string.on;
                else
                    resID = R.string.off;
                break;

            case Constants.BLINDS:
                if (device.getState().status.equals("opening"))
                    resID = R.string.opening;
                else if (device.getState().status.equals("closing"))
                    resID = R.string.closing;
                else if (device.getState().status.equals("opened"))
                    resID = R.string.opened;
                else
                    resID = R.string.closed;
                break;
            case Constants.DOOR:
                if (device.getState().status.equals("opened"))
                    resID = R.string.opened;
                else
                    resID = R.string.closed;
                break;
            case Constants.REFRIGERATOR:
                if (device.getState().mode.equals("default"))
                    resID = R.string.default_mode;
                else if (device.getState().mode.equals("party"))
                    resID = R.string.party;
                else
                    resID = R.string.vacation;
                break;
            case Constants.ALARM:
                if (device.getState().status.equals("armedStay"))
                    resID = R.string.armed_stay_status;
                else if (device.getState().status.equals("armedAway"))
                    resID = R.string.armed_away_status;
                else
                    resID = R.string.disarmed_status;
                break;
        }

        if (resID == 0)
            return "";
        return mContext.getString(resID);
    }
}
