package garcia.ioc.kebook.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.models.Book;

public class BookViewHolder extends RecyclerView.ViewHolder {

    private final TextView isbn;
    private final TextView title;
    private final TextView author;
    private final TextView synopsis;
    private final TextView genre;
    private final TextView available;
    private final TextView reserva;
    private String reservasLibro;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        isbn = itemView.findViewById(R.id.isbn_fila);
        title = itemView.findViewById(R.id.title_fila);
        author = itemView.findViewById(R.id.author_fila);
        synopsis = itemView.findViewById(R.id.synopsis_fila);
        genre = itemView.findViewById(R.id.genre_fila);
        available = itemView.findViewById(R.id.available_fila);
        reserva = itemView.findViewById(R.id.reserva_fila);
    }

    void bindRow(@NonNull Book book, BookAdapter.OnItemClickListener listener, String token) throws ExecutionException, InterruptedException {
        isbn.setText("ISBN: " + book.getIsbn());
        title.setText("Títol: "+ book.getTitulo());
        author.setText("Autor: " + String.valueOf(book.getAutor().getNombre()));
        synopsis.setText("Sinopsi: " + book.getSinopsis());
        genre.setText("Gènere: " + book.getGenero());
        available.setText("Disponible: " + String.valueOf(book.isDisponible()));
        reservasLibro = new AsyncManager().execute("obtenerReservasPorLibro", token, book.getIsbn()).get();
        if (reservasLibro.startsWith("[{")) {
            this.reserva.setText("Hay reservas?: " + "true");
        } else {
            this.reserva.setText("Hay reservas?: " + "false");
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    listener.onItemClick(book);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
