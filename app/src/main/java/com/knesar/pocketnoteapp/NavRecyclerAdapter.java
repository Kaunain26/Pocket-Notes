package com.knesar.navigationdemoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NavRecyclerAdapter extends ListAdapter<ManageNotes, NavRecyclerAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    protected NavRecyclerAdapter(OnItemClickListener listener) {
        super(Diff_Callback);
        this.listener = listener;
    }

    public static final DiffUtil.ItemCallback<ManageNotes> Diff_Callback = new DiffUtil.ItemCallback<ManageNotes>() {
        @Override
        public boolean areItemsTheSame(@NonNull ManageNotes oldItem, @NonNull ManageNotes newItem) {
            return oldItem.getId1() == newItem.getId1();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ManageNotes oldItem, @NonNull ManageNotes newItem) {
            return oldItem.getItems().equals(newItem.getItems());
        }
    };

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.nav_items, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ManageNotes manageNotes = getItem(position);
        holder.newItems.setText(manageNotes.getItems());
    }

   public ManageNotes getNoteAt(int position){
        return getItem(position);
    }
    public interface OnItemClickListener {
        void onItemClick(int Position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView newItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newItems = itemView.findViewById(R.id.childItems);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}