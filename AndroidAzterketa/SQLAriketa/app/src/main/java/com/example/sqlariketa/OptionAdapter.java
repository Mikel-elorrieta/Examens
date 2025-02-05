package com.example.sqlariketa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {

    private List<ProgramazioLengoaia> elements;
    private OnItemClickListener listener;

    public OptionAdapter(List<ProgramazioLengoaia> elements) {
        this.elements = elements;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_adapter, parent, false);
        return new OptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        ProgramazioLengoaia element = elements.get(position);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(element);
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public static class OptionViewHolder extends RecyclerView.ViewHolder {


        public OptionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProgramazioLengoaia item);
    }
}
