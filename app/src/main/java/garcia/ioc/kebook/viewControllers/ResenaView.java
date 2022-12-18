package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.models.Book;
import garcia.ioc.kebook.models.User;

public class ResenaView extends AppCompatActivity {

    private TextView titulo;
    private TextView autor;
    private Button resena;
    private EditText editTextResena;
    private String token;
    private String isbn;
    private String idUsuario;
    private String hayResenas;
    private String response;
    private String id;
    private JSONObject resenaObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resena);

        titulo = findViewById(R.id.titulo_book);
        autor = findViewById(R.id.autor_book);
        resena = findViewById(R.id.btn_resena);
        editTextResena = findViewById(R.id.edit_text_resena);

        titulo.setText("Titol: " + getIntent().getStringExtra("titulo"));
        autor.setText("Autor: " + getIntent().getStringExtra("autor"));

        try {
            token = getIntent().getStringExtra("token");
            response = new AsyncManager().execute("getUserWithId", token, getIdFromToken(token)).get();
            Gson gson = new Gson();
            User user = gson.fromJson(response, User.class);
            response = new AsyncManager().execute("obtenerLibroPorIsbn", token, getIntent().getStringExtra("isbn")).get();
            Book book = gson.fromJson(response, Book.class);

            resenaObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            userObject.put("id", user.getId());
            userObject.put("nombre", user.getNombre());
            userObject.put("correo", user.getCorreo());
            userObject.put("contrasena", user.getContrasena());
            userObject.put("fecha_creacion", user.getFecha_creacion());
            userObject.put("admin", String.valueOf(user.isAdmin()));
            resenaObject.put("usuario", userObject);
            JSONObject bookObject = new JSONObject();
            bookObject.put("isbn", book.getIsbn());
            bookObject.put("titulo", book.getTitulo());
            JSONObject autorObject = new JSONObject();
            autorObject.put("nombre", book.getAutor().getNombre());
            bookObject.put("autor", autorObject);
            bookObject.put("sinopsis", book.getSinopsis());
            bookObject.put("genero", book.getGenero());
            bookObject.put("disponible", String.valueOf(book.isDisponible()));
            resenaObject.put("libro", bookObject);

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void publicarResena(View view) throws JSONException, ExecutionException, InterruptedException {
        String textResena = editTextResena.getText().toString();
        resenaObject.put("resena", textResena);
        String resenaString = resenaObject.toString();
        response = new AsyncManager().execute("guardarResena", token, resenaString).get();
        if ((response.contains("200"))) {
            Toast.makeText(getApplicationContext(), "Reseña guardada correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se ha podido guardar la reseña", Toast.LENGTH_LONG).show();
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
}