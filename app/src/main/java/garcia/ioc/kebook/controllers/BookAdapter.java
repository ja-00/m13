package garcia.ioc.kebook.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.models.Book;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private Book[] data;

    public BookAdapter(Book[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fila_book = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_book, parent, false);
        return new BookViewHolder(fila_book);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bindRow(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}
