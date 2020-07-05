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

public class FragmentRecyclerAdapter extends ListAdapter<NotesEntity, FragmentRecyclerAdapter.MyViewHolder> {

    OnItemClickListener listener;

    protected FragmentRecyclerAdapter() {
        super(Diff_Callback);
    }

    public static final DiffUtil.ItemCallback<NotesEntity> Diff_Callback = new DiffUtil.ItemCallback<NotesEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotesEntity oldItem, @NonNull NotesEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotesEntity oldItem, @NonNull NotesEntity newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
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
        NotesEntity notesEntity = getItem(position);
        holder.title.setText(notesEntity.getTitle());
        holder.deleteItem.setVisibility(View.GONE);
        holder.parent.setBackgroundColor(notesEntity.getColor());
    }

    public NotesEntity getNoteAt(int position) {
        return getItem(position);

    }

    public interface OnItemClickListener {
        void onItemClick(NotesEntity note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView deleteItem;
        RelativeLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
            deleteItem = itemView.findViewById(R.id.deleteTittle);
            parent = itemView.findViewById(R.id.note_item_relativeLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });

        }
    }
}
