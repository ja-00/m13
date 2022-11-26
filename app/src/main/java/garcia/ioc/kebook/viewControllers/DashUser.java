package garcia.ioc.kebook.viewControllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.controllers.BookAdapter;
import garcia.ioc.kebook.models.Book;
import garcia.ioc.kebook.models.User;

/**
 * Clase que crea y controla el dashboard del usuario
 */
public class DashUser extends AppCompatActivity implements ChangePassDialogFragment.DialogListener{
    private String token = null;
    private String id = null;
    private String oldP = null;
    private String response = null;
    private User user = null;
    private String userEmail = null;
    private String idByEmail = null;
    private TextView idView;
    private TextView nameView;
    private TextView emailView;
    private TextView dateCreationView;
    private TextView isAdminView;
    private RecyclerView recyclerView = null;
    private String extraKey = null;
    private String extraValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_user);
        // TODO Revisar warning
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Kebook");

        //Obtener datos pasados por el activity que ha lanzado el actual
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
            userEmail = extras.getString("userEmail");
        }

        // Obtener datos del usuario actual para mostrar en cabecera del dashboard
        id = getIdFromToken(token);
        try {
            response = new AsyncManager().execute("getUserWithId", token, id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Convertir a objeto User el String con los datos de usuario devueltos por la consulta
        Gson gson = new Gson();
        user = gson.fromJson(response, User.class);
        oldP = user.getContrasena();
        // Mostrar los datos del usuario en el dashuser
        idView = findViewById(R.id.id);
        idView.setText("Id d'usuari: " + id);
        nameView = findViewById(R.id.nom);
        nameView.setText("Nom d'usuari: " + user.getNombre());
        emailView = findViewById(R.id.email);
        emailView.setText("Correu: " + user.getCorreo());
        dateCreationView = findViewById(R.id.data_creacio);
        /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        dateCreationView.setText("Data de creació: " + format.format(user.getFecha_creacion()));*/
        dateCreationView.setText("Data de creació: " + user.getFecha_creacion());
        isAdminView = findViewById(R.id.is_admin);
        isAdminView.setText("Es administrador?: " + user.isAdmin());
        // Enlazar con la view (recyclerview) donde se mostrará lista de libros
        recyclerView = findViewById(R.id.recyclerView_books);

        try {
            response = new AsyncManager().execute("booksList", token).get();
            showBooksList(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Benvingut usuari", Toast.LENGTH_LONG).show();
    }

    /**
     * Menú para el dashboard
     *
     * @param menu Menú al que aplicar el inflater (Convertir el recurso XML en elemento programable)
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_user:
                try {
                    response = new AsyncManager().execute("deleteUser", token, id).get();
                    if (response.contains("true")) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Compte eliminat correctament", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No s'ha pogut eliminar el compte", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.change_pass:
                /*Lanzar el dialogFragment*/
                ChangePassDialogFragment changePassDialogFragment = new ChangePassDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("notAlertDialog", true);
                changePassDialogFragment.setArguments(bundle);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment previ = getSupportFragmentManager().findFragmentByTag("dialog");
                if (previ != null) {
                    ft.remove(previ);
                }
                ft.addToBackStack(null);
                changePassDialogFragment.show(ft, "dialog");

                break;
            case R.id.logout:
                try {

                    response = new AsyncManager().execute("logout", id).get();
                    Log.d("Info:", "En DashUser, response: " + response);
                    if (response.contains("true")) {
                        Toast.makeText(getApplicationContext(), "Sessió tancada correctament", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "No s'ha pogut tancar sessió", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

    /**
     * Acción si se pulsa el botón de Atràs en el teléfono
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showWarningDialog();
    }

    /**
     * Cuadro de aviso al pulsar el botón de Atrás del tele´fono
     */
    private void showWarningDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //builder.setMessage("Vols sortir de l'aplicació?");

        //Mensaje de advertencia
        builder.setMessage("Vols sortir de l'aplicació?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Acción si se pulsa Si
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Acción si se pulsa No
                        Toast.makeText(getApplicationContext(), "Cancelar", Toast.LENGTH_SHORT).show();
                    }
                });
        // Crear el mensaje de alerta
        AlertDialog alert = builder.create();
        // Título
        alert.setTitle("Atenció");
        alert.show();
    }

    public void showBooksList(String response) {
        Gson gson = new Gson();
        Book[] books;
        if ((response.startsWith("{"))) {
            response = "[" + response + "]";
        }
        books = gson.fromJson(response, Book[].class);
        recyclerView.setAdapter(new BookAdapter(books, new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book item) {
                Intent bookItem = new Intent(getApplicationContext(), BookItem.class);
                bookItem.putExtra("token", token);
                bookItem.putExtra("tipo_usuario", "user");
                bookItem.putExtra("isbn", item.getIsbn());
                bookItem.putExtra("titulo", item.getTitulo());
                bookItem.putExtra("autor", item.getAutor().getNombre());
                bookItem.putExtra("sinopsis", item.getSinopsis());
                bookItem.putExtra("genero", item.getGenero());
                bookItem.putExtra("disponible", String.valueOf(item.isDisponible()));
                startActivity(bookItem);
            }
        }, token));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void filterList(View view) {
        Intent filter = new Intent(this, FilterBooks.class);
        startActivityForResult(filter,1);
        //filterBooksResultLauncher.launch(filter);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                extraKey = data.getStringExtra("key");
                extraValue = data.getStringExtra("value");
                try {
                    response = new AsyncManager().execute("filterBooksList", token, extraKey, extraValue).get();
                    showBooksList(response);
                } catch (ExecutionException | InterruptedException e) {
                    //e.printStackTrace();
                    Log.d ("info",  "No se han encontrado resultados");
                }
                Log.d("Info:", "Key devuelto por el filtro... " + extraKey);
                Log.d("Info:", "Value devuelto por el filtro... " + extraValue);
            }
        }
    }
    @Override
    public void onFinishEditDialog(String pass1, String pass2) throws ExecutionException, InterruptedException {
        if (pass1.equals(pass2)) {
            response = new AsyncManager().execute("getUserWithId", token, id).get();
            Gson gson = new Gson();
            user = gson.fromJson(response, User.class);
            oldP = user.getContrasena();
            response = new AsyncManager().execute("changePassword", token, id, oldP, pass1).get();
            if (response.contains("true")) {
                Toast.makeText(getApplicationContext(), "Contrasenya canviada correctament", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No s'ha pogut canviar la contrasenya", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Les contrasenyes introduides no son iguals", Toast.LENGTH_LONG).show();
        }

    }


    /*public void booksList(View view) throws ExecutionException, InterruptedException {

        response = new AsyncManager().execute("booksList", token).get();
        Gson gson = new Gson();
        Book[] books = gson.fromJson(response, Book[].class);
        Log.d("Info: ", "Array de libros " + books.toString());
        recyclerView.setAdapter(new BookAdapter(books));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

    }*/
}