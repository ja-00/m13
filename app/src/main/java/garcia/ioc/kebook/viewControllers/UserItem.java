package garcia.ioc.kebook.viewControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.R;
import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.models.User;

public class UserItem extends AppCompatActivity {

    private String token = null;
    private String id = null;
    private User user = null;
    private String response = null;
    private TextView idView;
    private TextView nameView;
    private TextView emailView;
    private TextView dateCreationView;
    private TextView isAdminView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_item);

        token = getIntent().getStringExtra("admin_token");

        // Mostrar los datos del usuario en el dashadmin
        idView = findViewById(R.id.id);
        id = String.valueOf(getIntent().getLongExtra("id", 0));
        idView.setText("Id d'usuari: " + id);
        nameView = findViewById(R.id.nom);
        nameView.setText("Nom d'usuari: " + getIntent().getStringExtra("nombre"));
        emailView = findViewById(R.id.email);
        emailView.setText("Correu: " + getIntent().getStringExtra("correo"));
        dateCreationView = findViewById(R.id.data_creacio);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        dateCreationView.setText("Data de creació: " + getIntent().getStringExtra("fecha_creacion"));
        isAdminView = findViewById(R.id.is_admin);
        isAdminView.setText("Es administrador?: " + getIntent().getBooleanExtra("es_admin", false));

    }

    public void deleteUser(View view) throws ExecutionException, InterruptedException {
            response = new AsyncManager().execute("deleteUser", token, id).get();
            if (response.contains("true")) {
                Toast.makeText(getApplicationContext(), "El usuario ha sido eliminado", Toast.LENGTH_LONG).show();
            }
            finish();
    }
}