package com.knesar.navigationdemoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomCategoryAdapter extends RecyclerView.Adapter<CustomCategoryAdapter.MyViewHolder> {

    List<CustomCategoryModelClass> listItems;
    OnItemClickListener listener;

    public CustomCategoryAdapter(List<CustomCategoryModelClass> listItems) {
        this.listItems = listItems;
    }
    //    protected CustomCategoryAdapter() {
//        super(Diff_Callback);
//    }
//
//    public static final DiffUtil.ItemCallback<CustomCategoryModelClass> Diff_Callback = new DiffUtil.ItemCallback<CustomCategoryModelClass>() {
//        @Override
//        public boolean areItemsTheSame(@NonNull CustomCategoryModelClass oldItem, @NonNull CustomCategoryModelClass newItem) {
//            return oldItem.getId() == newItem.getId();
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull CustomCategoryModelClass oldItem, @NonNull CustomCategoryModelClass newItem) {
//            return oldItem.getTask().equals(newItem.getTask());
//        }
//    };

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomCategoryModelClass currentItems = listItems.get(position);
        holder.task.setText(currentItems.getTask());
        holder.parent.setBackgroundColor(currentItems.getColor());
        holder.deleteImg.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public void addItem(CustomCategoryModelClass modelClass) {
        listItems.add(modelClass);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int pos,CustomCategoryModelClass note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView task;
        private ImageView deleteImg;
        private RelativeLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.text_view_title);
            deleteImg = itemView.findViewById(R.id.deleteTittle);
            parent = itemView.findViewById(R.id.note_item_relativeLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(pos,listItems.get(pos));
                    }
                }
            });

        }
    }
}
