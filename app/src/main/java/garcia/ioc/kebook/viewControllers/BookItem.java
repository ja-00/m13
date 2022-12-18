package garcia.ioc.kebook.viewControllers;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.models.Book;
import garcia.ioc.kebook.models.Evento;
import garcia.ioc.kebook.models.Resena;
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
    private Button confirmRecogi;
    private Button eliminarLibro;
    private Button resenaButton;
    private Button propEvento;
    private boolean available = false;
    private TextView hayReservas;
    private String reservasPorLibro = null;
    private String textResena = null;
    private String fechaPropuesta = null;

    JSONObject reservaObject = new JSONObject();
    JSONObject userObject = new JSONObject();
    JSONObject bookObject = new JSONObject();
    JSONObject autorObject = new JSONObject();
    JSONObject eventoObject = new JSONObject();


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
        confirmRecogi = findViewById(R.id.btn_confirm_check_out);
        //eliminarLibro = findViewById(R.id.btn_resena);
        resenaButton = findViewById(R.id.btn_resena);
        propEvento = findViewById(R.id.btn_proponer_evento);

        userType = getIntent().getStringExtra("tipo_usuario");

        if (userType.contains("admin")) {
            resenaButton.setVisibility(View.GONE);
            reservar.setVisibility(View.GONE);
            propEvento.setVisibility(View.GONE);
            try {
                reservasPorLibro = new AsyncManager().execute("obtenerReservasPorLibro", token, getIntent().getStringExtra("isbn")).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if (getIntent().getStringExtra("disponible").contains("true")) {
                confirmDevol.setAlpha(.5f);
                confirmDevol.setClickable(false);
                confirmRecogi.setAlpha(.5f);
                confirmRecogi.setClickable(false);
            } else if (!(reservasPorLibro.contains("null")) && (reservasPorLibro.startsWith("[{"))) {
                Gson gson = new Gson();
                Reserva[] reservas = gson.fromJson(reservasPorLibro, Reserva[].class);
                if (reservas[reservas.length - 1].isRecogido() && !reservas[reservas.length - 1].isDevuelto() && (reservas[0] != null)) {
                    confirmRecogi.setAlpha(.5f);
                    confirmRecogi.setClickable(false);
                }
                if (!reservas[reservas.length - 1].isRecogido() && !reservas[reservas.length - 1].isDevuelto() && (reservas[0] != null)) {
                    confirmDevol.setAlpha(.5f);
                    confirmDevol.setClickable(false);
                }
            }
        } else if (userType.contains("user")) {
            confirmRecogi.setVisibility(View.GONE);
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
            // Si no hay reserva el servidor devuelve para hayReservasDeEsteUsuario --> "[]\"
            //if (!(hayReservasDeEsteUsuario.contains("null")) && ((hayReservasDeEsteUsuario.startsWith("[")) || (hayReservasDeEsteUsuario.startsWith("[{")))) {
            if (!(hayReservasDeEsteUsuario.contains("null")) && (hayReservasDeEsteUsuario.startsWith("[{"))) {
                Gson gson = new Gson();
                Reserva[] reservas = gson.fromJson(hayReservasDeEsteUsuario, Reserva[].class);
                if (!(reservas[reservas.length - 1].isDevuelto()) && (reservas[0] != null)) {
                    reservar.setAlpha(.5f);
                    reservar.setClickable(false);
                }
                if (!(reservas[0].isRecogido()) && (reservas[0] != null)) {
                    resenaButton.setAlpha(.5f);
                    resenaButton.setClickable(false);
                }
            } else if (!(hayReservasDeEsteUsuario.contains("null")) && (hayReservasDeEsteUsuario.startsWith("[]"))) {
                resenaButton.setAlpha(.5f);
                resenaButton.setClickable(false);
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

    public void confirmarDevolucion(View view) throws ExecutionException, InterruptedException {
        response = new AsyncManager().execute("obtenerReservasPorLibro", token, getIntent().getStringExtra("isbn")).get();
        Reserva[] reservas;
        Gson gson = new Gson();
        reservas = gson.fromJson(response, Reserva[].class);
        response = new AsyncManager().execute("confirmarDevolucion", token, String.valueOf(reservas[reservas.length - 1].getId())).get();
        finish();
    }

    public void confirmarRecogida(View view) throws ExecutionException, InterruptedException {
        response = new AsyncManager().execute("obtenerReservasPorLibro", token, getIntent().getStringExtra("isbn")).get();
        Reserva[] reservas;
        Gson gson = new Gson();
        reservas = gson.fromJson(response, Reserva[].class);
        response = new AsyncManager().execute("confirmarRecogida", token, String.valueOf(reservas[reservas.length - 1].getId())).get();
        finish();
    }

    public void reservarLibro(View view) throws ExecutionException, InterruptedException, JSONException {
        //Reserva reserva = new Reserva();
        response = new AsyncManager().execute("getUserWithId", token, getIdFromToken(token)).get();
        Gson gson = new Gson();
        user = gson.fromJson(response, User.class);
        response = new AsyncManager().execute("obtenerLibroPorIsbn", token, getIntent().getStringExtra("isbn")).get();
        book = gson.fromJson(response, Book.class);
        //reservaObject = new JSONObject();
        //userObject = new JSONObject();
        userObject.put("id", user.getId());
        userObject.put("nombre", user.getNombre());
        userObject.put("correo", user.getCorreo());
        userObject.put("contrasena", user.getContrasena());
        userObject.put("fecha_creacion", user.getFecha_creacion());
        userObject.put("admin", String.valueOf(user.isAdmin()));
        reservaObject.put("usuario", userObject);
        //bookObject = new JSONObject();
        bookObject.put("isbn", book.getIsbn());
        bookObject.put("titulo", book.getTitulo());
        //autorObject = new JSONObject();
        autorObject.put("nombre", book.getAutor().getNombre());
        bookObject.put("autor", autorObject);
        bookObject.put("sinopsis", book.getSinopsis());
        bookObject.put("genero", book.getGenero());
        bookObject.put("disponible", String.valueOf(book.isDisponible()));
        reservaObject.put("libro", bookObject);
        LocalDate localDate;
        localDate = LocalDate.now();
        reservaObject.put("fecha_inicio", localDate.toString());
        reservaObject.put("fecha_fin", localDate.toString());
        reservaObject.put("devuelto", "false");
        reservaObject.put("recogido", "false");

        String reservaJson = reservaObject.toString();

        response = new AsyncManager().execute("reservarLibro", token, reservaJson).get();
        if ((response.contains("200"))) {

            Toast.makeText(getApplicationContext(), "Reserva realizada correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se ha podido realizar la reserva", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    public String getIdFromToken(String token) {
        String[] splitToken = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(splitToken[0]));
        String payload = new String(decoder.decode(splitToken[1]));
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

    public void resenaLibro(View view) throws ExecutionException, InterruptedException, JSONException {
        Intent resenaActivity = new Intent(getApplicationContext(), ResenaView.class);
        resenaActivity.putExtra("token", token);
        resenaActivity.putExtra("isbn", getIntent().getStringExtra("isbn"));
        startActivity(resenaActivity);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
/*        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                textResena = data.getStringExtra("text_resena");
            }
        }*/
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {

                fechaPropuesta = data.getStringExtra("fecha_propuesta");

                try {
                    response = new AsyncManager().execute("getUserWithId", token, getIdFromToken(token)).get();
                    Gson gson = new Gson();
                    user = gson.fromJson(response, User.class);
                    response = new AsyncManager().execute("obtenerLibroPorIsbn", token, getIntent().getStringExtra("isbn")).get();
                    book = gson.fromJson(response, Book.class);
                    userObject.put("id", user.getId());
                    userObject.put("nombre", user.getNombre());
                    userObject.put("correo", user.getCorreo());
                    userObject.put("contrasena", user.getContrasena());
                    userObject.put("fecha_creacion", user.getFecha_creacion());
                    userObject.put("admin", String.valueOf(user.isAdmin()));
                    eventoObject.put("proponente", userObject);
                    bookObject.put("isbn", book.getIsbn());
                    bookObject.put("titulo", book.getTitulo());
                    autorObject.put("nombre", book.getAutor().getNombre());
                    bookObject.put("autor", autorObject);
                    bookObject.put("sinopsis", book.getSinopsis());
                    bookObject.put("genero", book.getGenero());
                    bookObject.put("disponible", String.valueOf(book.isDisponible()));
                    eventoObject.put("libro", bookObject);
                    eventoObject.put("fecha", fechaPropuesta);

                    String eventoJson = eventoObject.toString();
                    response = new AsyncManager().execute("guardarEvento", token, eventoJson).get();
                } catch (ExecutionException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }

/*                String returnedResult = data.getData().toString();
                // OR
                // String returnedResult = data.getDataString();*/
            }
        }
    }

    public void proponerEvento(View view) {
        Intent eventoActivity = new Intent(getApplicationContext(), EventoView.class);
        eventoActivity.putExtra("titulo", getIntent().getStringExtra("titulo"));
        eventoActivity.putExtra("autor", getIntent().getStringExtra("autor"));
        startActivityForResult(eventoActivity, 2);
    }


}