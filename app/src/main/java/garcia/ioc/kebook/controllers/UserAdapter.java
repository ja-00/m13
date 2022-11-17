package garcia.ioc.kebook.controllers;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.models.User;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private final OnItemClickListener listener;
    private User[] data;

    public UserAdapter(User[] data, OnItemClickListener listener) {
        this.listener = listener;
        this.data = data;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fila = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila, parent, false);
        return new UserViewHolder(fila);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
    holder.bindRow(data[position], listener);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public interface OnItemClickListener {
        void onItemClick(User item);
    }
}
