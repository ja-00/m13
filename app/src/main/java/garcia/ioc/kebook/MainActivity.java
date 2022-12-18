package garcia.ioc.kebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import garcia.ioc.kebook.controllers.AsyncManager;
import garcia.ioc.kebook.viewControllers.DashAdmin;
import garcia.ioc.kebook.viewControllers.DashUser;
import garcia.ioc.kebook.viewControllers.Registre;

/**
 * Clase principal
 */
public class MainActivity extends AppCompatActivity {

    private EditText mUserEmail;
    private EditText mPass;
    private Button mButtonLogin;
    private Switch mSwitchAdmin;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserEmail = findViewById(R.id.inputUser);
        mPass = findViewById(R.id.inputPassword);
        mButtonLogin = findViewById(R.id.buttonLogin);
        mSwitchAdmin = findViewById(R.id.switchAdmin);

        verifyStoragePermissions(this);
    }

    /**
     * @param view Vista del botón Login. android:onClick
     * @throws ExecutionException   Excepcion
     * @throws InterruptedException Método login. Controlador de la pulsación del botón.
     *                              Conecta con el server y resuelve si la conexión ha sido correcta
     */
    public void login(View view) throws ExecutionException, InterruptedException {
        // Obtener los String de userEmail y password de los editText y declarar e inicializar las variables
        String userEmail = mUserEmail.getText().toString();
        String pass = mPass.getText().toString();
        String jwToken;
        Intent dashUser = new Intent(this, DashUser.class);
        Intent dashAdmin = new Intent(this, DashAdmin.class);

        // Conexión con el server
        if (mSwitchAdmin.isChecked()) {
            //jwToken = new LoginAsync().execute(userEmail, pass, "admin").get();
            jwToken = new AsyncManager().execute("login", userEmail, pass, "admin").get();
            if ((jwToken.toLowerCase().contains("error")) || jwToken.isEmpty()) {
                showDialogError();
            } else {
                // Colocar datos para pasar al activity y lanzarlo
                dashAdmin.putExtra("token", jwToken);
                startActivity(dashAdmin);
                finish();
            }
        } else {
            jwToken = new AsyncManager().execute("login", userEmail, pass, "user").get();
            if ((jwToken.toLowerCase().contains("error")) || jwToken.isEmpty()) {
                showDialogError();
            } else {
                // Colocar datos para pasar al activity y lanzarlo
                dashUser.putExtra("token", jwToken);
                dashUser.putExtra("userEmail", userEmail);
                startActivity(dashUser);
                finish();
            }
        }
        Log.d("Info", "En MainActivity.login. Token rebut: " + jwToken);
    }

    /**
     * Ventana de alerta si la conexión no ha sido correcta
     */
    private void showDialogError() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        //builder.setMessage("Vols sortir de l'aplicació?");

        //Mensaje a mostrar y acción con la opción positiva
        builder.setMessage("Usuari i/o password no son correctes")
                .setCancelable(false)
                .setPositiveButton("Sortir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Acción botón sortir, salir de la aplicacióin
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("Tornar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Accíón botón Tornar, vuelve a login
                        Toast.makeText(getApplicationContext(), "Cancelar", Toast.LENGTH_SHORT).show();
                    }
                });
        // Crear caja de alerta
        AlertDialog alert = builder.create();
        alert.setTitle("Atenció");
        alert.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void registre(View view) {
        Intent registre = new Intent(this, Registre.class);
        startActivity(registre);
    }

    // For API 23+ you need to request the read/write permissions even if they are already in your manifest.


    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}

