package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Base64;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.models.Book;
import garcia.ioc.kebook.models.Escritor;
import garcia.ioc.kebook.models.Reserva;
import garcia.ioc.kebook.models.User;

public class BookItem extends AppCompatActivity {

    private String token = null;
    private String userType = null;
    private User user;
    private Book book;
    private String id = null;
    private String response = null;
    private String hayReservasDeEsteUsuario;
    private TextView isbnView;
    private TextView titleView;
    private TextView autorView;
    private TextView sinopsisView;
    private TextView generoView;
    private TextView disponibleView;
    private Button reservar;
    private Button confirmDevol;
    private Button confirmReserv;
    private Button eliminarLibro;
    private Button resena;
    private boolean available = false;
    private TextView hayReservas;
    private Reserva reserva = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_item);

        token = getIntent().getStringExtra("token");

        isbnView = findViewById(R.id.isbn_book);
        titleView = findViewById(R.id.titulo_book);
        autorView = findViewById(R.id.autor_book);
        sinopsisView = findViewById(R.id.sinopsis_book);
        generoView = findViewById(R.id.genero_book);
        disponibleView = findViewById(R.id.disponible_book);
        hayReservas = findViewById(R.id.hay_reservas_book);

        isbnView.setText("ISBN " + getIntent().getStringExtra("isbn"));
        titleView.setText("Títol: " + getIntent().getStringExtra("titulo"));
        autorView.setText("Autor: " + getIntent().getStringExtra("autor"));
        sinopsisView.setText("Sinopsi: " + getIntent().getStringExtra("sinopsis"));
        generoView.setText("Gènere: " + getIntent().getStringExtra("genero"));
        disponibleView.setText("Disponible: " + getIntent().getStringExtra("disponible"));
        hayReservas.setText("Hay reservas?: " + getIntent().getStringExtra("hay_reservas"));


        reservar = findViewById(R.id.btn_book_book);
        confirmDevol = findViewById(R.id.btn_confirm_return);
        confirmReserv = findViewById(R.id.btn_confirm_check_out);
        //eliminarLibro = findViewById(R.id.btn_resena);
        resena = findViewById(R.id.btn_resena);

        userType = getIntent().getStringExtra("tipo_usuario");

        if (userType.contains("admin")) {
            eliminarLibro.setVisibility(View.GONE);
            reservar.setVisibility(View.GONE);
            if ((getIntent().getStringExtra("disponible").contains("true"))) {
                confirmDevol.setAlpha(.5f);
                confirmDevol.setClickable(false);
            }
            if (!((getIntent().getStringExtra("disponible").contains("true")) && (getIntent().getStringExtra("hay_reservas").contains("true"))) || (getIntent().getStringExtra("recogido").contains("true"))) {
                confirmReserv.setAlpha(.5f);
                confirmReserv.setClickable(false);
            }
        } else if (userType.contains("user")) {
            confirmReserv.setVisibility(View.GONE);
            confirmDevol.setVisibility(View.GONE);
            if (getIntent().getStringExtra("disponible").contains("false")) {
                reservar.setAlpha(.5f);
                reservar.setClickable(false);
            }
            try {
                hayReservasDeEsteUsuario = new AsyncManager().execute("obtenerReservasLibroPorUsuario", token, getIntent().getStringExtra("isbn"), getIdFromToken(token)).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            // TODO Revisar Warning de comparar strings !=
            if ((hayReservasDeEsteUsuario != "null") && ((hayReservasDeEsteUsuario.startsWith("[")) || (hayReservasDeEsteUsuario.startsWith("[")))) {
                Gson gson = new Gson();
                Reserva[] reservas = gson.fromJson(hayReservasDeEsteUsuario, Reserva[].class);
                if (!(reservas[0].isRecogido()) && (reservas[0] != null)) {
                    resena.setAlpha(.5f);
                    resena.setClickable(false);
                }
            }
        }
    }

    public boolean checkBookAvailable() throws ExecutionException, InterruptedException {
        boolean available = false;
        response = new AsyncManager().execute("checkBookAvailable", token, getIntent().getStringExtra("isbn")).get();
        if (response.contains("true")) {
            available = true;
        }
        return available;
    }

    public void confirmarDevolucion(View view) {
    }

    public void confirmarPrestamo(View view) throws ExecutionException, InterruptedException {
        response = new AsyncManager().execute("getBooksOfBook", token, getIntent().getStringExtra("isbn")).get();
        Reserva[] reservas;
        Gson gson = new Gson();
        reservas = gson.fromJson(response, Reserva[].class);
        // TODO Revisar Warning de getId
        reservas[0].getId();
        response = new AsyncManager().execute("confirmarRecogida", token, String.valueOf(reservas[0].getId())).get();
        finish();
    }


    public void reservarLibro(View view) throws ExecutionException, InterruptedException, JSONException {
        reserva = new Reserva();
        response = new AsyncManager().execute("getUserWithId", token, getIdFromToken(token)).get();
        Gson gson = new Gson();
        user = gson.fromJson(response, User.class);
        response = new AsyncManager().execute("obtenerLibroPorIsbn", token, getIntent().getStringExtra("isbn")).get();
        book = gson.fromJson(response, Book.class);
        JSONObject reservaObject = new JSONObject();
        JSONObject userObject = new JSONObject();
        userObject.put("id", user.getId());
        userObject.put("nombre", user.getNombre());
        userObject.put("correo", user.getCorreo());
        userObject.put("contrasena", user.getContrasena());
        userObject.put("fecha_creacion", user.getFecha_creacion());
        userObject.put("admin", String.valueOf(user.isAdmin()));
        reservaObject.put("usuario", userObject);
        JSONObject bookObject = new JSONObject();
        bookObject.put("isbn", book.getIsbn());
        bookObject.put("titulo", book.getTitulo());
        JSONObject autorObject = new JSONObject();
        autorObject.put("nombre", book.getAutor().getNombre());
        bookObject.put("autor", autorObject);
        bookObject.put("sinopsis", book.getSinopsis());
        bookObject.put("genero", book.getGenero());
        bookObject.put("disponible", String.valueOf(book.isDisponible()));
        reservaObject.put("libro", bookObject);
        LocalDate localDate;
        localDate = LocalDate.now();
        reservaObject.put("fecha_inicio",localDate.toString());
        reservaObject.put("fecha_fin",localDate.toString());
        reservaObject.put("devuelto", "false");
        reservaObject.put("recogido", "false");

        String reservaJson = reservaObject.toString();

        response = new AsyncManager().execute("reservarLibro", token, reservaJson).get();
        if ((response.contains("200"))) {
            Toast.makeText(getApplicationContext(), "Reserva realizada correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se ha podido realizar la reserva", Toast.LENGTH_LONG).show();
        }
    }

    public String getIdFromToken(String token) {
        String[] splitToken = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(splitToken[0]));
        String payload = new String(decoder.decode(splitToken[1]));
        Log.d("Info: ", "Payload del recibido por el activity dashUser: " + payload);
        Gson JSONPayload = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(payload);
            id = jsonObject.getString("jti");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void resena(View view) {
        Intent resena = new Intent(getApplicationContext(), ResenaView.class);
        resena.putExtra("libro", getIntent().getStringExtra("titulo"));
        resena.putExtra("autor", getIntent().getStringExtra("autor"));
        startActivity(resena);
        finish();
    }


}