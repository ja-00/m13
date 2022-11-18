package garcia.ioc.kebook.viewControllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.controllers.BookAdapter;
import garcia.ioc.kebook.controllers.UserAdapter;
import garcia.ioc.kebook.models.Book;
import garcia.ioc.kebook.models.User;

/**
 * Clase que crea y gestiona el dashboard del usuario admin
 */
public class DashAdmin extends AppCompatActivity implements ChangePassDialogFragment.DialogListener {

    String token = null;
    String id = null;
    private User user = null;
    private String oldP = null;
    String response = null;
    private TextView idView;
    private TextView nameView;
    private TextView emailView;
    private TextView dateCreationView;
    private TextView isAdminView;
    boolean confirm = false;
    RecyclerView recyclerView = null;
    private String extraKey = null;
    private String extraValue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Kebook");

        //Obtener datos pasados por el activity que ha lanzado el actual
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
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
        // Mostrar los datos del usuario en el dashadmin
        idView = findViewById(R.id.id);
        idView.setText("Id d'usuari: " + id);
        nameView = findViewById(R.id.nom);
        nameView.setText("Nom d'usuari: " + user.getNombre());
        emailView = findViewById(R.id.email);
        emailView.setText("Correu: " + user.getCorreo());
        dateCreationView = findViewById(R.id.data_creacio);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        dateCreationView.setText("Data de creació: " + format.format(user.getFecha_creacion()));
        isAdminView = findViewById(R.id.is_admin);
        isAdminView.setText("Es administrador?: " + user.isAdmin());
        // Enlazar con la view (recyclerview) donde se mostrará lista de usuarios
        recyclerView = findViewById(R.id.recyclerView_books);

        try {
            response = new AsyncManager().execute("booksList", token).get();
            showBooksList(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Benvingut Administrador", Toast.LENGTH_SHORT).show();
    }

    /**
     * Menú para el dashboard
     *
     * @param menu Menú al que aplicar el inflater (Convertir el recurso XML en elemento programable)
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override/**/
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_user:
                /*showWarnDialog("Estàs a punt de eliminar el teu compte. Vols continuar?", "delete");*/
                Log.d("Info: ", "Dentro de optionsMenu. Confirm = " + String.valueOf(confirm));

                try {
                    response = new AsyncManager().execute("deleteUser", token, id).get();
                    if (response.contains("true")) {
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
                    Log.d("Info:", "En DashAdmin, response: " + response);
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
     * Si se pulsa el botón de Atràs en el teléfono
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showWarningDialog("Vols sortir de l'aplicació?", "exit");
    }

    /**
     * Cuadro de aviso al pulsar el botón de Atrás del tele´fono
     */
    private void showWarningDialog(String message, String option) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Setting message manually and performing action on button click
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                        //exit application
                        if (option.contains("exit")) {
                            finish();
                            System.exit(0);
                        } else if (option.contains("delete")) {
                            return;
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'No' Button
                        Toast.makeText(getApplicationContext(), "Cancelar", Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Atenció");
        alert.show();
    }

    public void showBooksList(String response) throws ExecutionException, InterruptedException {
        Gson gson = new Gson();
        Book[] books;
        if ((response.startsWith("{"))) {
            response = "[" + response + "]";
        }
        books = gson.fromJson(response, Book[].class);
        recyclerView.setAdapter(new BookAdapter(books));
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

    public void usersList(View view) {
        try {
            response = new AsyncManager().execute("usersList", token).get();
            Gson gson = new Gson();
            User[] users;
            users = gson.fromJson(response, User[].class);
            recyclerView.setAdapter(new UserAdapter(users, new UserAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(User item) {
                    Intent userItem = new Intent(getApplicationContext(), UserItem.class);
                    userItem.putExtra("admin_token", token);
                    userItem.putExtra("id", item.getId());
                    userItem.putExtra("nombre", item.getNombre());
                    userItem.putExtra("correo", item.getCorreo());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String fecha = format.format(item.getFecha_creacion());
                    userItem.putExtra("fecha_creacion", fecha);
                    userItem.putExtra("es_admin", item.isAdmin());
                    startActivity(userItem);
                }
            }));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void booksList(View view) throws ExecutionException, InterruptedException {
        response = new AsyncManager().execute("booksList", token).get();
        showBooksList(response);
    }
}