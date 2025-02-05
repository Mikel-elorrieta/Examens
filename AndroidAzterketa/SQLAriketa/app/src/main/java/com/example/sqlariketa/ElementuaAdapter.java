package com.example.sqlariketa;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ElementuaAdapter extends RecyclerView.Adapter<ElementuaAdapter.ElementuaViewHolder> {
    private List<ProgramazioLengoaia> lengoaienZerrenda;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ProgramazioLengoaia item);
    }

    public ElementuaAdapter(List<ProgramazioLengoaia> lengoaienZerrenda, OnItemClickListener listener) {
        this.lengoaienZerrenda = lengoaienZerrenda;
        this.listener = listener;
    }

    public static class ElementuaViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtIzena, txtDeskribapena, txtSoftwareLibrea;

        public ElementuaViewHolder(View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtIzena = itemView.findViewById(R.id.txtIzena);
            txtDeskribapena = itemView.findViewById(R.id.txtDeskribapena);
            txtSoftwareLibrea = itemView.findViewById(R.id.txtSoftwareLibre);
        }
    }

    @Override
    public ElementuaAdapter.ElementuaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adapter, parent, false);
        return new ElementuaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ElementuaViewHolder holder, int position) {
        ProgramazioLengoaia currentElementua = lengoaienZerrenda.get(position);

        holder.txtId.setText(String.valueOf(currentElementua.getID()));
        holder.txtIzena.setText(currentElementua.getIzena());
        holder.txtDeskribapena.setText(currentElementua.getDeskribapena());

        if (currentElementua.isSoftwareLibrea()) {
            holder.txtSoftwareLibrea.setText(R.string.softwareLibrea);
        } else {
            holder.txtSoftwareLibrea.setText("");
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentElementua));
    }

    @Override
    public int getItemCount() {
        return lengoaienZerrenda.size();
    }
}
