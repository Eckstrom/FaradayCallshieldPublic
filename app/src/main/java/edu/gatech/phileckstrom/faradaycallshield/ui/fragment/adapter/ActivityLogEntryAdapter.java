package edu.gatech.phileckstrom.faradaycallshield.ui.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;

//Custom recyclerview adapter class
public class ActivityLogEntryAdapter extends ListAdapter<ActivityLogEntryAdapter.DataHolder, ActivityLogEntryAdapter.BasViewHolder> {

    public ActivityLogEntryAdapter() {

        super(new DiffUtil.ItemCallback<DataHolder>() {
            @Override
            public boolean areItemsTheSame(DataHolder oldItem, DataHolder newItem) {
                return oldItem != null && newItem != null && oldItem.idEquals(newItem);
            }

            @Override
            public boolean areContentsTheSame(DataHolder oldItem, DataHolder newItem) {
                return oldItem != null && oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public BasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ?  R.layout.header_item : R.layout.item_log_final, parent, false);
        return viewType == 0 ? new ActivityLogHeaderViewHolder(view) : new ActivityLogViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof HeaderDataHolder ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull BasViewHolder holder, int position) {

        final Object data = getItem(position).data;

        if (holder instanceof ActivityLogViewHolder) {
            ActivityLogViewHolder tempHolder = (ActivityLogViewHolder) holder;
            ActivityLogItem currentItem = (ActivityLogItem) data;
            tempHolder.caller.setText(currentItem.caller);
            tempHolder.date.setText(currentItem.time);
            tempHolder.rejection.setVisibility(View.VISIBLE);
        } else if (holder instanceof ActivityLogHeaderViewHolder) {
            ActivityLogHeaderViewHolder tempHolder = (ActivityLogHeaderViewHolder) holder;
            tempHolder.header.setText((String) data);
        }

    }

    public static abstract class DataHolder<T> {
        T data;

        DataHolder(T data) {
            this.data = data;
        }

        abstract boolean idEquals(DataHolder other);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataHolder<?> that = (DataHolder<?>) o;
            return Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }
    }

    public static class HeaderDataHolder extends DataHolder<String> {

        public HeaderDataHolder(String data) {
            super(data);
        }

        @Override
        boolean idEquals(DataHolder other) {
            return data.equals(other.data);
        }
    }

    public static class ActivityLogDataHolder extends DataHolder<ActivityLogItem> {

        public ActivityLogDataHolder(ActivityLogItem data) {
            super(data);
        }

        @Override
        boolean idEquals(DataHolder other) {
            return other instanceof ActivityLogDataHolder && data.id == ((ActivityLogDataHolder) other).data.id;
        }
    }


    abstract static class BasViewHolder extends RecyclerView.ViewHolder {

        BasViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class ActivityLogViewHolder extends BasViewHolder {
        ImageView rejection;
        TextView caller;
        TextView date;

        ActivityLogViewHolder(View itemView) {
            super(itemView);

            rejection = itemView.findViewById(R.id.log_blocktype);
            rejection.setImageResource(R.drawable.phone_block);
            caller = itemView.findViewById(R.id.log_number);
            date = itemView.findViewById(R.id.log_final_date);
        }
    }

    static class ActivityLogHeaderViewHolder extends BasViewHolder {
        TextView header;

        ActivityLogHeaderViewHolder(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.header_field);
        }
    }
}
