package com.knesar.navigationdemoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ManageActivityRecyclerAdapter extends ListAdapter<ManageNotes, ManageActivityRecyclerAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    protected ManageActivityRecyclerAdapter() {
        super(Diff_Callback);
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
        View v = inflater.inflate(R.layout.note_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ManageNotes manageNotes = getItem(position);
        holder.parent.setBackgroundColor(manageNotes.getColor());
        holder.newItems.setText(manageNotes.getItems());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemCLick(position);
            }
        });

    }

    public ManageNotes getNoteAt(int pos) {
        return getItem(pos);
    }

    public interface OnItemClickListener {
        void onLongCLick(ManageNotes note);
        void onItemCLick(int pos);
    }

    public void setOnItemCLickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView newItems;
        private RelativeLayout parent;
        private ImageView deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newItems = itemView.findViewById(R.id.text_view_title);
            parent = itemView.findViewById(R.id.note_item_relativeLayout);
            deleteButton = itemView.findViewById(R.id.deleteTittle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                         listener.onLongCLick(getItem(position));
                    }
                }
            });
        }
    }
}
