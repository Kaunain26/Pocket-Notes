package com.knesar.navigationdemoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

class Archives_recyclerView extends ListAdapter<Archives, Archives_recyclerView.MyViewHolder> {

    protected Archives_recyclerView() {
        super(Diff_Callback);
    }

    protected static final DiffUtil.ItemCallback<Archives> Diff_Callback = new DiffUtil.ItemCallback<Archives>() {
        @Override
        public boolean areItemsTheSame(@NonNull Archives oldItem, @NonNull Archives newItem) {
            return oldItem.getId1() == newItem.getId1();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Archives oldItem, @NonNull Archives newItem) {
            return oldItem.getItems().equals(newItem.getItems()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.archives_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Archives archives = getItem(position);
        holder.title.setText(archives.getItems());
        holder.description.setText(archives.getDescription());
        holder.parent.setBackgroundColor(archives.getColor());
    }

    public Archives getNoteAt(int pos) {
        return getItem(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView description;
        RelativeLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.archive_text_view_title);
            description = itemView.findViewById(R.id.archive_text_view_description);
            parent = itemView.findViewById(R.id.archive_note_item_relativeLayout);

        }
    }
}
