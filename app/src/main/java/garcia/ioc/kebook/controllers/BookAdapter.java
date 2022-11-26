package garcia.ioc.kebook.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.models.Book;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private final OnItemClickListener listener;
    private Book[] data;
    private String token;


    public BookAdapter(Book[] data, OnItemClickListener listener, String token) {
        this.listener = listener;
        this.data = data;
        this.token = token;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fila_book = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_book, parent, false);
        return new BookViewHolder(fila_book);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        try {
            holder.bindRow(data[position], listener, token);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public interface OnItemClickListener {
        void onItemClick(Book item) throws ExecutionException, InterruptedException;
    }
}
